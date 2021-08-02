package com.zgd.demo.acitiviti;

import com.alibaba.fastjson.JSON;
import com.zgd.demo.activiti.dto.Holiday;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.CompleteTaskPayloadBuilder;
import org.activiti.engine.history.HistoricTaskInstance;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * RunTaskTest
 *
 * @author zgd
 * @date 2021/8/2 11:32
 */
public class RunTaskTest extends BaseTest{



  @Test
  public void findTask() {
    //设置主题为张三, 查张三的任务
    securityUtil.logInAs("zhangsan");
    //设置任务负责人 ACT_RUN_TASK
    Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 10));
    if (tasks.getTotalItems() > 0) {
      List<Task> content = tasks.getContent();
      for (Task task : content) {
        System.out.println("任务ID:" + task.getId());
        System.out.println("任务名称:" + task.getName());
        System.out.println("任务的创建时间:" + task.getCreatedDate());
        System.out.println("任务的办理人:" + task.getAssignee());
        System.out.println("流程实例ID：" + task.getProcessInstanceId());
        System.out.println("流程定义ID:" + task.getProcessDefinitionId());
        System.out.println("getOwner:" + task.getOwner());
        System.out.println("getDescription:" + task.getDescription());
        System.out.println("getFormKey:" + task.getFormKey());
      }
    }
  }

  @Test
  public void queryTask2() {
    //根据流程定义的key,负责人assignee来实现当前用户的任务列表查询
    List<org.activiti.engine.task.Task> list = tasksService.createTaskQuery()
            .processDefinitionKey("process_qingjia4")
            .taskAssignee("zhaoliu")
            .list();
    for (org.activiti.engine.task.Task task : list) {
      System.out.println("任务ID:" + task.getId());
      System.out.println("任务名称:" + task.getName());
      System.out.println("任务的创建时间:" + task.getCreateTime());
      System.out.println("任务的办理人:" + task.getAssignee());
      System.out.println("流程实例ID：" + task.getProcessInstanceId());
      System.out.println("执行对象ID:" + task.getExecutionId());
      System.out.println("流程定义ID:" + task.getProcessDefinitionId());
      System.out.println("领取时间:" + task.getClaimTime());
      System.out.println("TenantId:" + task.getTenantId());
      System.out.println("getOwner:" + task.getOwner());
      System.out.println("getCategory:" + task.getCategory());
      System.out.println("getDescription:" + task.getDescription());
      System.out.println("getFormKey:" + task.getFormKey());
      Map<String, Object> map = task.getProcessVariables();
      for (Map.Entry<String, Object> m : map.entrySet()) {
        System.out.println("key:" + m.getKey() + " value:" + m.getValue());
      }
      for (Map.Entry<String, Object> m : task.getTaskLocalVariables().entrySet()) {
        System.out.println("key:" + m.getKey() + " value:" + m.getValue());
      }

    }
  }

  /**
   * 在完成任务时设置变量
   */
  @Test
  public void completeTaskWithVariable() {
    //任务ID ACT_RU_TASK表
    String taskId = "5a919b16-f37f-11eb-b8bc-00ff64da1685";
    List<org.activiti.engine.task.Task> list = tasksService.createTaskQuery().taskId(taskId).list();
    if (!list.isEmpty()){
      org.activiti.engine.task.Task task = list.get(0);
      String executionId = task.getExecutionId();
      System.out.println("executionId = " + executionId);
      //setVariable是全局的,带local则是本次的
      Holiday holiday = new Holiday();
      holiday.setDays(4);
      // 针对本分支上的节点设置流程变量，如果一个流程中已经走完2个活动节点，对这2个活动节点都设置流程变量，即使流程变量的名称相同，
      // 后一次的版本的值也不会替换前一次版本的值.
      // 它会使用已走完的不同的任务ID作为标识，存放2个流程变量值，而且可以看到TASK_ID的字段会存放任务ID的值
      tasksService.setVariableLocal(taskId,"holiday", holiday);
      tasksService.setVariableLocal(taskId,"本地local8", "本地测试变量8");
      //不管哪个分支,唯一变量,后面再设置会替换
      tasksService.setVariable(taskId,"全局globalname8", "全局测试变量8");
      tasksService.complete(taskId);
      System.out.println("完成任务：任务ID：" + taskId);
    }
  }


  @Test
  public void completeTaskWithVariable2(){
    String taskId = "f3a9470c-f381-11eb-b78b-00ff64da1685";
    org.activiti.engine.task.Task task = tasksService.createTaskQuery().taskId(taskId).singleResult();
    Map<String, Object> processVariables = task.getProcessVariables();
    System.out.println("JSON.toJSONString(processVariables) = " + JSON.toJSONString(processVariables));
    Map<String, Object> taskLocalVariables = task.getTaskLocalVariables();
    System.out.println("JSON.toJSONString(taskLocalVariables) = " + JSON.toJSONString(taskLocalVariables));
//    tasksService.setVariableLocal(taskId,"本地local8", "本地测试变量8");
//    tasksService.setVariable(taskId,"全局变量8", "全局变量8");
//    tasksService.complete(taskId);
  }

  /**
   * 查询并完成任务
   *
   * @author 赖柄沣 bingfengdev@aliyun.com
   * @date 2020-08-18 13:41:40
   * @version 1.0
   */
  @Test
  public void findAndFinishTask() {
    securityUtil.logInAs("zhaoliu");
    Page<Task> taskPage = taskRuntime.tasks(Pageable.of(0, 10));

    if (taskPage.getTotalItems() > 0) {
      List<Task> content = taskPage.getContent();
      for (Task task : content) {
        System.out.println("查到任务id:" + task.getId());
        System.out.println("查到任务执行人:" + task.getAssignee());
        System.out.println("查到任务流程实例id:" + task.getProcessInstanceId());
        System.out.println("查到任务流程定义id:" + task.getProcessDefinitionId());

        //领取任务,只能在assignee 为null时
//        taskRuntime.claim(new ClaimTaskPayloadBuilder()
//                .withTaskId(task.getId())
//                .build());
        //完成任务
        taskRuntime.complete(new CompleteTaskPayloadBuilder()
                .withTaskId(task.getId())
                .build());
      }
    }

  }


  /**
   * 历史活动实例查询
   */
  @Test
  public void queryHistoryTask() {
    List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery() // 创建历史活动实例查询
            .processInstanceId("666a867a-f337-11eb-aa30-00ff64da1685") // 执行流程实例id
            .orderByTaskCreateTime()
            .asc()
            .list();
    for (HistoricTaskInstance hai : list) {
      System.out.println("活动ID:" + hai.getId());
      System.out.println("流程实例ID:" + hai.getProcessInstanceId());
      System.out.println("活动名称：" + hai.getName());
      System.out.println("办理人：" + hai.getAssignee());
      System.out.println("开始时间：" + hai.getStartTime());
      System.out.println("结束时间：" + hai.getEndTime());
    }
  }
}
