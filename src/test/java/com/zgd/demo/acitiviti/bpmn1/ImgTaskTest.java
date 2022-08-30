package com.zgd.demo.acitiviti.bpmn1;

import com.zgd.demo.acitiviti.BaseTest;
import com.zgd.demo.activiti.util.ActivitiUtil;
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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * TraceTaskTest
 *
 * @author zgd
 */
public class ImgTaskTest extends BaseTest {

  @Test
  public void geProcessDiagram() throws FileNotFoundException {
    String filepath = "D:\\aa.png";
    String processInstanceId = "bc889f47-5c5f-11ea-863b-1c1b0d7b318e";
    ActivitiUtil.getFlowImgByInstanceId(processInstanceId, new FileOutputStream(filepath), true);
    System.out.println("图片生成成功");
  }
}
