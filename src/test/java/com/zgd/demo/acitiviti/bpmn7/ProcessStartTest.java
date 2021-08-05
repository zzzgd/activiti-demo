package com.zgd.demo.acitiviti.bpmn7;

import com.zgd.demo.acitiviti.BaseTest;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.model.payloads.StartProcessPayload;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ProcessStartTest
 * 启用流程测试
 * @author zgd
 * @date 2021/8/2 11:32
 */
public class ProcessStartTest extends BaseTest {

  /**
   * 启动流程
   *
   * @author 赖柄沣 bingfengdev@aliyun.com
   * @date 2020-08-18 13:21:12
   * @version 1.0
   */
  @Test
  public void startProcess() {

    StartProcessPayload myProcess = ProcessPayloadBuilder
            .start()
//            .withProcessDefinitionId("myProcess_1:1:5c5e0de3-e112-11ea-a73b-287fcf13e373")
            .withProcessDefinitionKey("qingjia7")
            //这里的id取自于ACT_RE_PROCDEF
//            .withProcessDefinitionId("leave:5:5d3899a5-f120-11eb-ad90-00ff64da1685")
//            .withVariable("deptLeader","张三")
            .withVariable("qidong_var","12")
            .build();

    myProcess.setName("请假的流程");
//    myProcess.setProcessInstanceName("张三请假");
    processRuntime.start(myProcess);
    //流程实例3bfc5ab0-7ee8-4fba-8b91-bf3615527336
    //流程实例c6d4751f-8847-47b7-8cc6-4293552439e9
    //流程实例36efe7a8-7f42-4758-ad76-9d64494fa8ee
    //流程实例9b1dcd09-f11e-4800-b10f-6e68eaee3178
    System.out.println("流程实例id" + myProcess.getId());
    System.out.println("流程定义id" + myProcess.getProcessDefinitionId());
    System.out.println("流程定义key" + myProcess.getProcessDefinitionKey());

  }

  /**
   * 启动一个实例,动态设置设置assignee
   */
  @Test
  public void startProcessInstance() {
    //设置assignee,map键对应配置中的变量名
    Map<String,Object> map=new HashMap<>();
    map.put("user","bumen");
    map.put("user2","jingli");
    map.put("user3","ceo");
    map.put("user4","hr");
//    map.put("qidong_var","启动时变量");

    //启动流程实例，同时还要设置流程定义的assignee的值
    RuntimeService runtimeService = processEngine.getRuntimeService();
    ProcessInstance myProcess = runtimeService.startProcessInstanceByKey("qingjia7", map);
    System.out.println("流程实例id" + myProcess.getId());
    System.out.println("流程定义id" + myProcess.getProcessDefinitionId());
    System.out.println("流程定义key" + myProcess.getProcessDefinitionKey());
    List<Task> list = tasksService.createTaskQuery().processDefinitionId(myProcess.getProcessDefinitionId()).active().list();
    Task task = list.stream().sorted((o1, o2) -> (int) (o1.getCreateTime().getTime() - o2.getCreateTime().getTime())).findFirst().get();
//    Task task = tasksService.createTaskQuery().processDefinitionId(myProcess.getProcessDefinitionId()).singleResult();
    System.out.println("task.getId() = " + task.getId());
    System.out.println("task.getExecutionId() = " + task.getExecutionId());
  }


  /**
   * 删除流程实例
   */
  @Test
  public void deleteProcessInstance() {
    //执行删除流程定义  参数代表流程部署的id
    processRuntime.delete(ProcessPayloadBuilder.delete().withProcessInstanceId("8e6fca7b-f06b-11eb-b047-00ff64da1685").withReason("不想申请了").build());
    System.out.println("删除流程实例");
  }


  /**
   * 单个流程实例的挂起，激活
   */
  @Test
  public void activateProcessInstanceById() {
    //查询流程实例对象
    RuntimeService runtimeService = processEngine.getRuntimeService();
    ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId("1651c0f4-f06d-11eb-9195-00ff64da1685").singleResult();

    //当前流程定义的实例是否都为暂停状态
    boolean suspended = processInstance.isSuspended();

    String processInstanceId = processInstance.getId();
    if (suspended) {
      //激活
      runtimeService.activateProcessInstanceById(processInstanceId);
      System.out.println("流程：" + processInstanceId + "激活");
    } else {
      //挂起
      runtimeService.suspendProcessInstanceById(processInstanceId);
      System.out.println("流程：" + processInstanceId + "挂起");
    }
  }

  /**
   * 挂起流程实例的方法2
   */
  @Test
  public void suspendProcess() {
    org.activiti.api.process.model.ProcessInstance.ProcessInstanceStatus status = processRuntime.processInstance("1651c0f4-f06d-11eb-9195-00ff64da1685").getStatus();
    if (status == org.activiti.api.process.model.ProcessInstance.ProcessInstanceStatus.SUSPENDED) {
      processRuntime.resume(ProcessPayloadBuilder.resume("1651c0f4-f06d-11eb-9195-00ff64da1685"));
      System.out.println("恢复流程实例");
    } else {
      processRuntime.suspend(ProcessPayloadBuilder.suspend("1651c0f4-f06d-11eb-9195-00ff64da1685"));
      System.out.println("挂起流程实例");
    }
  }
}
