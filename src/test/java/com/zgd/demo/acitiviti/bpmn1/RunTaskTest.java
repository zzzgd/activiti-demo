package com.zgd.demo.acitiviti.bpmn1;

import com.alibaba.fastjson.JSON;
import com.zgd.demo.acitiviti.BaseTest;
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
 */
public class RunTaskTest extends BaseTest {



  @Test
  public void findTask() {
    //设置主题为张三, 查张三的任务
    securityUtil.logInAs("zhangsan");
//    securityUtil.logInAs("lisi");
//    securityUtil.logInAs("wangwu");
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
   * 查询并完成任务
   *
   * @version 1.0
   */
  @Test
  public void findAndFinishTask() {
    securityUtil.logInAs("zhangsan");
//    securityUtil.logInAs("lisi");
    Page<Task> taskPage = taskRuntime.tasks(Pageable.of(0, 10));

    if (taskPage.getTotalItems() > 0) {
      List<Task> content = taskPage.getContent();
      for (Task task : content) {
        System.out.println("查到任务id:" + task.getId());
        System.out.println("查到任务执行人:" + task.getAssignee());
        System.out.println("查到任务流程实例id:" + task.getProcessInstanceId());
        System.out.println("查到任务流程定义id:" + task.getProcessDefinitionId());
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
    //act_hi_taskinst
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
