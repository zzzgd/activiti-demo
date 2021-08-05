package com.zgd.demo.acitiviti.bpmn7;

import com.zgd.demo.acitiviti.BaseTest;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * TraceTaskTest
 *
 * @author zgd
 * @date 2021/8/2 17:50
 */
public class TraceTaskTest extends BaseTest {


  /**
   * 获取来路
   */
  public List<String> getIncomeFlow(String assignee){
    RepositoryService repositoryService = processEngine.getRepositoryService();
    RuntimeService runtimeService = processEngine.getRuntimeService();

    List<String> strList = new ArrayList<String>();
    Task task = tasksService.createTaskQuery()
            .processDefinitionKey("process_qingjia5").taskAssignee(assignee).singleResult();
    ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) repositoryService
            .getProcessDefinition(task.getProcessDefinitionId());
    //获取流程实例
    ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
            .processInstanceId(task.getProcessInstanceId()).singleResult();
    //获取当前流程实例的活动id
    String activityId = processInstance.getActivityId();

    //获取当前模型
    BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
// 获取当前节点
    FlowElement flowElement = bpmnModel.getFlowElement(task.getTaskDefinitionKey());
// 这里不转也有方法拿到，我这是为了后人阅读方便
    UserTask userTask = (UserTask)flowElement;
//获取节点出口线段
    List<SequenceFlow> outgoingFlows = userTask.getOutgoingFlows();
    for (SequenceFlow outgoingFlow : outgoingFlows) {
      System.out.println(outgoingFlow.getName());
      strList.add(outgoingFlow.getName());
    }
    return strList;
  }

  /**
   * 获取当前节点的来路
   * ① 获得当前节点的所有来路（其中一条是来路）
   * ② 拿到上一步的节点（历史节点），找到它的所有出路（其中有一条出路是当前节点的来路）
   * ③ 找到以上两步中的重复的连线就是当前节点的来路
   */
  @Test
  public void getIncomingFlow(){
    RepositoryService repositoryService = processEngine.getRepositoryService();
    RuntimeService runtimeService = processEngine.getRuntimeService();
    String assignee = "ceo";
    List<String> preActivityOutcomeList = new ArrayList<String>();
    Task task = tasksService.createTaskQuery().processDefinitionKey("process_qingjia5")
            .taskAssignee(assignee).singleResult();
    List<HistoricActivityInstance> historicActivityInstanceList = historyService
            .createHistoricActivityInstanceQuery()
            .processInstanceId(task.getProcessInstanceId())
            .finished()//已经结束的活动节点
            .orderByHistoricActivityInstanceStartTime().desc().list();
    for(HistoricActivityInstance historicActivityInstance: historicActivityInstanceList){
      System.out.println("历史活动ID: "+historicActivityInstance.getActivityId());
      System.out.println("历史活动名称: "+historicActivityInstance.getActivityName());
    }
    //获取当前节点的前一个活动节点
    HistoricActivityInstance historicActivityInstance = historicActivityInstanceList.get(0);
    //获取前一个活动节点的id
    String activityId = historicActivityInstance.getActivityId();
    //获取流程定义实体的对象(不需要创建查询对象*****)
    ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) repositoryService
            .getProcessDefinition(task.getProcessDefinitionId());

    //获取当前节点的所有入路
    List<String> incomeFlows = this.getIncomeFlow(assignee);
    System.out.println(incomeFlows);

    String incomeFlow = null;
    loop : for(String income: incomeFlows){
      for(String outcome: preActivityOutcomeList){
        if(income.equals(outcome)){
          incomeFlow = income;//incomeFlow = outcome;
          break loop;
        }
      }
    }
    System.out.println("当前节点的来路是: "+incomeFlow);
  }


  /**
   * 查询历史流程变量
   */
  @Test
  public void queryHistoricLocalVariables() {
    HistoricTaskInstanceQuery historicTaskInstanceQuery = historyService.createHistoricTaskInstanceQuery();
    List<HistoricTaskInstance> list = historicTaskInstanceQuery.includeTaskLocalVariables()
            .finished().list();

    for (HistoricTaskInstance hti : list) {
      System.out.println("============================");
      System.out.println("任务id:" + hti.getId());
      System.out.println("任务名称:" + hti.getName());
      System.out.println("任务负责人:" + hti.getAssignee());
      System.out.println("任务local变量：" + hti.getTaskLocalVariables());
    }
  }
}
