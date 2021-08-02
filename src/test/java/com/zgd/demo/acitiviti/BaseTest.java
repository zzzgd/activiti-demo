package com.zgd.demo.acitiviti;

import com.zgd.demo.activiti.Application;
import com.zgd.demo.activiti.conf.SecurityUtil;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.TaskService;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * BaseTest
 *
 * @author zgd
 * @date 2021/8/2 11:29
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class BaseTest {
  @Autowired
  public ProcessRuntime processRuntime;
  @Autowired
  public TaskRuntime taskRuntime;
  @Autowired
  public TaskService tasksService;
  @Autowired
  public SecurityUtil securityUtil;
  @Autowired
  public HistoryService historyService;
  @Autowired
  public ProcessEngine processEngine;

  @Before
  public void init() {
    //认证
    securityUtil.logInAs("system");
  }
}
