package com.zgd.demo.acitiviti.bpmn9;

import com.zgd.demo.acitiviti.BaseTest;
import com.zgd.demo.activiti.dto.Holiday;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.model.payloads.StartProcessPayload;
import org.junit.Test;

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
    Holiday holiday = new Holiday();
    holiday.setDays(1);
    StartProcessPayload myProcess = ProcessPayloadBuilder
            .start()
//            .withProcessDefinitionId("myProcess_1:1:5c5e0de3-e112-11ea-a73b-287fcf13e373")
            .withProcessDefinitionKey("qingjia9-1")
            .withVariable("holiday",holiday)
            .build();

    myProcess.setName("请假的流程9-1");
    processRuntime.start(myProcess);
    System.out.println("流程实例id" + myProcess.getId());
    System.out.println("流程定义id" + myProcess.getProcessDefinitionId());
    System.out.println("流程定义key" + myProcess.getProcessDefinitionKey());

  }

}
