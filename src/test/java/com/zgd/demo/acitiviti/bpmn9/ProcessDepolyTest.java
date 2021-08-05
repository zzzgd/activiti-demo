package com.zgd.demo.acitiviti.bpmn9;

import com.zgd.demo.acitiviti.BaseTest;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.junit.Test;

/**
 * 测试流程部署部分
 */
public class ProcessDepolyTest extends BaseTest {

  /**
   * 部署流程
   * <p>说明：关于流程的部署，activiti7与springboot整合后，会自动部署已经创建好的流程文件（bpmn、png）</p>
   * <p>只要将流程定义文件存放在"resources/processes/下即可"</p>
   *
   * @author 赖柄沣 bingfengdev@aliyun.com
   * @date 2020-08-18 12:53:51
   * @version 1.0
   */
  @Test
  public void repositoryProcess() {
    RepositoryService repositoryService = processEngine.getRepositoryService();
    Deployment deployment = repositoryService.createDeployment()
            .addClasspathResource("bpmn/qingjia9.bpmn")
            .name("流程请假9-1")
            //部署, 插入数据库
            .deploy();
    System.out.println(deployment.getName());
    System.out.println(deployment.getId()); //8754f504-f06f-11eb-b2d2-00ff64da1685
  }

  @Test
  public void removeDeploy(){
    RepositoryService repositoryService = processEngine.getRepositoryService();
    repositoryService.deleteDeployment("51db3e34-f5c4-11eb-9068-00ff64da1685");

  }

}

