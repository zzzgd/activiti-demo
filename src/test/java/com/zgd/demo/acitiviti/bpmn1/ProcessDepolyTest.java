package com.zgd.demo.acitiviti.bpmn1;

import com.zgd.demo.acitiviti.BaseTest;
import org.activiti.api.process.model.ProcessDefinition;
import org.activiti.api.process.model.builders.GetProcessDefinitionsPayloadBuilder;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.junit.Test;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * 测试流程部署部分
 */
public class ProcessDepolyTest extends BaseTest {

  /**
   * 部署流程
   * <p>说明：关于流程的部署，activiti7与springboot整合后，会自动部署已经创建好的流程文件（bpmn、png）</p>
   * <p>只要将流程定义文件存放在"resources/processes/下即可"</p>
   *
   * @version 1.0
   */
  @Test
  public void repositoryProcess() {
    RepositoryService repositoryService = processEngine.getRepositoryService();
    Deployment deployment = repositoryService.createDeployment()
            .addClasspathResource("bpmn/qingjia1.bpmn")
            .addClasspathResource("bpmn/qingjia1.png")
            .name("流程请假模拟")
            //部署, 插入数据库
            .deploy();
    //部署后每条process都存在ACT_RE_DEPLOYMENT表中,资源名称存在ACT_RE_PROCDEF表中, 文件数据存在ACT_GE_BYTEARRAY表中
    System.out.println(deployment.getName());
    System.out.println(deployment.getId());
  }

  /**
   * 删除已经部署的流程定义 delete from ACT_RE_DEPLOYMENT 流程部署信息表; ACT_RE_PROCDEF 流程定义数据表; ACT_GE_BYTEARRAY 二进制数据表;
   * 1.当我们正在执行的这一套流程没有完全审批结束的时候，此时如果要删除流程定义信息就会失败。 2.如果要求强制删除,可以使用repositoryService.deleteDeployment("myProcess_1:1:f482ce07-35bf-11ea-b325-30b49ec7161f",true);
   * 参数true代表级联删除，此时就会先删除没有完成的流程结点，最后就可以删除流程定义信息 false的值代表不级联
   */
  @Test
  public void deleteProcessDefinition() {
    //执行删除流程定义  参数代表流程部署的id
    RepositoryService repositoryService = processEngine.getRepositoryService();
    repositoryService.deleteDeployment("bdb5a375-f066-11eb-ad99-00ff64da1685");
    System.out.println("删除部署流程");
  }


  /**
   * 操作部署流程的挂起、激活 查的ACT_RE_PROCDEF
   */
  @Test
  public void activateProcessDefinitionById() {
    RepositoryService repositoryService = processEngine.getRepositoryService();
    org.activiti.engine.repository.ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("process_qingjia2").singleResult();
    //当前流程定义的实例是否都为暂停状态
    boolean suspended = processDefinition.isSuspended();

    String processDefinitionId = processDefinition.getId();

    if (suspended) {
      //挂起状态则激活.
      repositoryService.activateProcessDefinitionById(processDefinitionId, true, new Date());
      System.out.println("流程定义：" + processDefinitionId + "激活");
    } else {
      //激活状态则挂起
      repositoryService.suspendProcessDefinitionById(processDefinitionId, true, new Date());
      System.out.println("流程定义：" + processDefinitionId + "挂起");
    }
  }


}

