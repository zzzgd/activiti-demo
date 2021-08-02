package com.zgd.demo.activiti.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * TaskListener
 *
 * @author zgd
 * @date 2021/8/2 14:19
 */
public class MyTaskListener implements TaskListener {

  @Override
  public void notify(DelegateTask delegateTask) {
    System.out.println("这里是task监听器");
    //监听: event:create
    //流程实例idcf13ee38-f35d-11eb-8388-00ff64da1685
    //流程定义idprocess_qingjia3:2:39286a39-f35d-11eb-acc3-00ff64da1685
    //流程定义keyprocess_qingjia3
    System.out.println("delegateTask = " + delegateTask);
    System.out.println("监听: event:" + delegateTask.getEventName());
    delegateTask.setDescription("监听器设置描述");
  }
}
