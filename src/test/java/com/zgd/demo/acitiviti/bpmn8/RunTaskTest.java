package com.zgd.demo.acitiviti.bpmn8;

import com.alibaba.fastjson.JSON;
import com.zgd.demo.acitiviti.BaseTest;
import com.zgd.demo.activiti.dto.Holiday;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.CompleteTaskPayloadBuilder;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.IdentityLink;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * RunTaskTest
 *
 * @author zgd
 * @date 2021/8/2 11:32
 */
public class RunTaskTest extends BaseTest {


  @Test
  public void findGroupTask() {
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

  /**
   * 即使表中看到Act_ru_task表中：assignee字段是空的, 但是组员能查到自己的任务
   */
  @Test
  public void queryTask2() {
    //根据流程定义的key,负责人assignee来实现当前用户的任务列表查询
    List<org.activiti.engine.task.Task> list = tasksService.createTaskQuery()
            .processDefinitionKey("qingjia8")
//            .taskAssignee("zhangsan") 不直接指定人来查
            .taskCandidateUser("zhangsan") //使用组员来查
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
   * 组员领取任务
   * 认领之后其他人员再查询时则不存在，而act_ru_task表中assignee字段为认领人 ，认领之后其他操作和一起都一样
   */
  @Test
  public void claim() {
    //任务ID
    String taskId = "d2b90328-f524-11eb-8cff-00ff64da1685";
    //个人任务的办理人
    String userId = "lisi";
    //领取任务 claim与setAssignee区别在于claim领取之后别人不可以再领取不然会报错而setAssignee则不然
    tasksService.claim(taskId, userId);
    // 被认领之后的任务可再扯回组任务（前提：之前这个任务是组任务），其他人员则还可以认领
//    tasksService.setAssignee(taskId,null);
    //组任务可以继续添加组参与人员：
    tasksService.addCandidateGroup(taskId,"xxx");
    System.out.println("ok");
  }


  /**
   * 查询组员
   */
  @Test
  public void findGroupCandidate(){
    //任务ID
    String taskId = "d2b90328-f524-11eb-8cff-00ff64da1685";
    List<IdentityLink>list = processEngine.getTaskService()//
            .getIdentityLinksForTask(taskId);
    if(list!=null && list.size()>0){
      for(IdentityLink identityLink : list){
        System.out.println("任务ID："+identityLink.getTaskId());
        System.out.println("用户ID："+identityLink.getUserId());
        System.out.println("-----------------------------------");
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
    if (!list.isEmpty()) {
      org.activiti.engine.task.Task task = list.get(0);
      String executionId = task.getExecutionId();
      System.out.println("executionId = " + executionId);
      //setVariable是全局的,带local则是本次的
      Holiday holiday = new Holiday();
      holiday.setDays(4);
      // 针对本分支上的节点设置流程变量，如果一个流程中已经走完2个活动节点，对这2个活动节点都设置流程变量，即使流程变量的名称相同，
      // 后一次的版本的值也不会替换前一次版本的值.
      // 它会使用已走完的不同的任务ID作为标识，存放2个流程变量值，而且可以看到TASK_ID的字段会存放任务ID的值
      tasksService.setVariableLocal(taskId, "holiday", holiday);
      tasksService.setVariableLocal(taskId, "本地local8", "本地测试变量8");
      //不管哪个分支,唯一变量,后面再设置会替换
      tasksService.setVariable(taskId, "全局globalname8", "全局测试变量8");
      tasksService.complete(taskId);
      System.out.println("完成任务：任务ID：" + taskId);
    }
  }


  @Test
  public void completeTaskWithVariable2() {
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
