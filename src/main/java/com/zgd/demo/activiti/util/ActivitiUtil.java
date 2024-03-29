package com.zgd.demo.activiti.util;

import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * ActivitiUtil
 *
 * @author zgd
 * @date 2022/8/29 18:56
 */
public class ActivitiUtil {

    private static Logger logger = LoggerFactory.getLogger(ActivitiUtil.class);

    /**
     * 流程定义相关操作
     */
    @Autowired
    private ProcessRuntime processRuntime;

    /**
     * 任务相关操作
     */
    @Autowired
    private TaskRuntime taskRuntime;


    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RuntimeService runtimeService;

    private static ActivitiUtil activitiUtil;
    /**
     * 根据流程实例Id,获取实时流程图片
     *
     * @param processInstanceId
     * @param outputStream
     * @param useCustomColor    true:用自定义的颜色（完成节点绿色，当前节点红色），default:用默认的颜色（红色）
     * @return
     */
    public static void getFlowImgByInstanceId(String processInstanceId, OutputStream outputStream, boolean useCustomColor) {
        try {
            if (StringUtils.isEmpty(processInstanceId)) {
                logger.error("processInstanceId is null");
                return;
            }
            // 获取历史流程实例
            HistoricProcessInstance historicProcessInstance = activitiUtil.historyService
                    .createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId).singleResult();
            // 获取流程中已经执行的节点，按照执行先后顺序排序
            List<HistoricActivityInstance> historicActivityInstances = activitiUtil.historyService
                    .createHistoricActivityInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .orderByHistoricActivityInstanceId()
                    .asc().list();
            // 高亮已经执行流程节点ID集合
            List<String> highLightedActivitiIds = new ArrayList<>();
            int index = 1;
            for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
                if (useCustomColor) {
                    // 用自定义颜色
                    if (index == historicActivityInstances.size()) {
                        // 当前节点
                        highLightedActivitiIds.add(historicActivityInstance.getActivityId() + "#");
                    } else {
                        // 已完成节点
                        highLightedActivitiIds.add(historicActivityInstance.getActivityId());
                    }
                } else {
                    // 用默认颜色
                    highLightedActivitiIds.add(historicActivityInstance.getActivityId());
                }

                index++;
            }

            ProcessDiagramGenerator processDiagramGenerator = null;
            if (useCustomColor) {
                // 使用自定义的程序图片生成器
                processDiagramGenerator = new CustomProcessDiagramGenerator();

            } else {
                // 使用默认的程序图片生成器
                processDiagramGenerator = new DefaultProcessDiagramGenerator();
            }


            BpmnModel bpmnModel = activitiUtil.repositoryService
                    .getBpmnModel(historicProcessInstance.getProcessDefinitionId());
            // 高亮流程已发生流转的线id集合
            List<String> highLightedFlowIds = getHighLightedFlows(bpmnModel, historicActivityInstances);

            // 使用默认配置获得流程图表生成器，并生成追踪图片字符流
            InputStream imageStream = processDiagramGenerator.generateDiagram(bpmnModel,
                    highLightedActivitiIds, highLightedFlowIds, "宋体",
                    "微软雅黑", "黑体");

            // 输出图片内容
            Integer byteSize = 1024;
            byte[] b = new byte[byteSize];
            int len;
            while ((len = imageStream.read(b, 0, byteSize)) != -1) {
                outputStream.write(b, 0, len);
            }
        } catch (Exception e) {
            logger.error("processInstanceId" + processInstanceId + "生成流程图失败，原因：" + e.getMessage(), e);
        }

    }

    /**
     * 获取已经流转的线
     *
     * @param bpmnModel
     * @param historicActivityInstances
     * @return
     */
    private static List<String> getHighLightedFlows(BpmnModel bpmnModel,
                                                    List<HistoricActivityInstance> historicActivityInstances) {
        // 高亮流程已发生流转的线id集合
        List<String> highLightedFlowIds = new ArrayList<>();
        // 全部活动节点
        List<FlowNode> historicActivityNodes = new ArrayList<>();
        // 已完成的历史活动节点
        List<HistoricActivityInstance> finishedActivityInstances = new ArrayList<>();

        for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
            FlowNode flowNode = (FlowNode) bpmnModel.getMainProcess()
                    .getFlowElement(historicActivityInstance.getActivityId(), true);
            historicActivityNodes.add(flowNode);
            if (historicActivityInstance.getEndTime() != null) {
                finishedActivityInstances.add(historicActivityInstance);
            }
        }

        FlowNode currentFlowNode = null;
        FlowNode targetFlowNode = null;
        // 遍历已完成的活动实例，从每个实例的outgoingFlows中找到已执行的
        for (HistoricActivityInstance currentActivityInstance : finishedActivityInstances) {
            // 获得当前活动对应的节点信息及outgoingFlows信息
            currentFlowNode = (FlowNode) bpmnModel.getMainProcess()
                    .getFlowElement(currentActivityInstance.getActivityId(), true);
            List<SequenceFlow> sequenceFlows = currentFlowNode.getOutgoingFlows();

            /**
             * 遍历outgoingFlows并找到已已流转的 满足如下条件认为已已流转：
             * 1.当前节点是并行网关或兼容网关，则通过outgoingFlows能够在历史活动中找到的全部节点均为已流转
             * 2.当前节点是以上两种类型之外的，通过outgoingFlows查找到的时间最早的流转节点视为有效流转
             */
            if ("parallelGateway".equals(currentActivityInstance.getActivityType())
                    || "inclusiveGateway".equals(currentActivityInstance.getActivityType())) {
                // 遍历历史活动节点，找到匹配流程目标节点的
                for (SequenceFlow sequenceFlow : sequenceFlows) {
                    targetFlowNode = (FlowNode) bpmnModel.getMainProcess()
                            .getFlowElement(sequenceFlow.getTargetRef(), true);
                    if (historicActivityNodes.contains(targetFlowNode)) {
                        highLightedFlowIds.add(targetFlowNode.getId());
                    }
                }
            } else {
                List<Map<String, Object>> tempMapList = new ArrayList<>();
                for (SequenceFlow sequenceFlow : sequenceFlows) {
                    for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
                        if (historicActivityInstance.getActivityId().equals(sequenceFlow.getTargetRef())) {
                            Map<String, Object> map = new HashMap<>(16);
                            map.put("highLightedFlowId", sequenceFlow.getId());
                            map.put("highLightedFlowStartTime", historicActivityInstance.getStartTime().getTime());
                            tempMapList.add(map);
                        }
                    }
                }

                if (!CollectionUtils.isEmpty(tempMapList)) {
                    // 遍历匹配的集合，取得开始时间最早的一个
                    long earliestStamp = 0L;
                    String highLightedFlowId = null;
                    for (Map<String, Object> map : tempMapList) {
                        long highLightedFlowStartTime = Long.valueOf(map.get("highLightedFlowStartTime").toString());
                        if (earliestStamp == 0 || earliestStamp >= highLightedFlowStartTime) {
                            highLightedFlowId = map.get("highLightedFlowId").toString();
                            earliestStamp = highLightedFlowStartTime;
                        }
                    }

                    highLightedFlowIds.add(highLightedFlowId);
                }

            }

        }
        return highLightedFlowIds;
    }




}
