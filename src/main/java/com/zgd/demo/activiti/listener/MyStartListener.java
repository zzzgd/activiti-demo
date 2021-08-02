package com.zgd.demo.activiti.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.TaskListener;

/**
 * TaskListener
 *
 * @author zgd
 * @date 2021/8/2 14:19
 */
public class MyStartListener implements ExecutionListener {


  @Override
  public void notify(DelegateExecution delegateExecution) {
    //delegateExecution = Execution[ id 'cf148a7e-f35d-11eb-8388-00ff64da1685' ] - activity 'StartEvent_1 - parent 'cf13ee38-f35d-11eb-8388-00ff64da1685'
    //启动监听:流程实例id = cf13ee38-f35d-11eb-8388-00ff64da1685
    //启动监听:流程定义id = process_qingjia3:2:39286a39-f35d-11eb-acc3-00ff64da1685
    //启动监听:getEventName = start
    System.out.println("这里是start监听器");
    System.out.println("delegateExecution = " + delegateExecution);
    System.out.println("启动监听:流程实例id = " + delegateExecution.getProcessInstanceId());
    System.out.println("启动监听:流程定义id = " + delegateExecution.getProcessDefinitionId());
    System.out.println("启动监听:getEventName = " + delegateExecution.getEventName());
  }
}
