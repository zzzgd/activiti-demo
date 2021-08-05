package com.zgd.demo.acitiviti.bpmn8;

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
   */
  @Test
  public void startProcess() {

    StartProcessPayload myProcess = ProcessPayloadBuilder
            .start()
//            .withProcessDefinitionId("myProcess_1:1:5c5e0de3-e112-11ea-a73b-287fcf13e373")
            .withProcessDefinitionKey("qingjia8")
            .withVariable("users","zhangsan,lisi,wangwu")
            .withVariable("user2","zhaoliu")
            .withVariable("user3","qianqi")
            .withVariable("user4","zhouba")
            .build();

    myProcess.setName("请假的流程8");
    processRuntime.start(myProcess);
    System.out.println("流程实例id" + myProcess.getId());
    System.out.println("流程定义id" + myProcess.getProcessDefinitionId());
    System.out.println("流程定义key" + myProcess.getProcessDefinitionKey());

  }

}
