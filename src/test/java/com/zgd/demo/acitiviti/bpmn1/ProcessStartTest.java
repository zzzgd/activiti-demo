package com.zgd.demo.acitiviti.bpmn1;

import com.zgd.demo.acitiviti.BaseTest;
import com.zgd.demo.activiti.dto.Holiday;
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
 */
public class ProcessStartTest extends BaseTest {


  /**
   * 启动一个实例,动态设置设置assignee
   * com.zgd.demo.activiti.conf.SecurityAuthConf.getUserDetailsService()
   */
  @Test
  public void startProcessInstance() {
    //设置assignee,map键对应配置中的变量名
    Map<String,Object> map=new HashMap<>();
    securityUtil.logInAs("xiaoming");

    Holiday holiday = new Holiday();
    holiday.setDays(1);

    map.put("user1","zhangsan");//部长
    map.put("user2","lisi");//经理
    map.put("user3","wangwu");//hr
    map.put("holiday",holiday);

    //启动流程实例，同时还要设置流程定义的assignee的值
    RuntimeService runtimeService = processEngine.getRuntimeService();
    ProcessInstance myProcess = runtimeService.startProcessInstanceByKey("qingjia1", map);
    System.out.println("流程实例id" + myProcess.getId());
    System.out.println("流程定义id" + myProcess.getProcessDefinitionId());
    System.out.println("流程定义key" + myProcess.getProcessDefinitionKey());
  }

}
