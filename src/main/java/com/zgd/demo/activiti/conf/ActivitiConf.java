package com.zgd.demo.activiti.conf;

import org.activiti.engine.*;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.impl.history.HistoryLevel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * ActivitiConf
 *
 * @author zgd
 * @date 2021/7/29 15:00
 */
//@Configuration
public class ActivitiConf {

  /**
   * 工作流流程引擎配置文件
   * @param dataSource
   * @return
   */
/*  @Bean
  public ProcessEngineConfiguration processEngineConfiguration(DataSource dataSource){
    StandaloneProcessEngineConfiguration standaloneProcessEngineConfiguration = new StandaloneProcessEngineConfiguration();
    standaloneProcessEngineConfiguration.setDataSource(dataSource);
    //自动更新表结构，数据库表不存在时会自动创建表
    standaloneProcessEngineConfiguration.setDatabaseSchemaUpdate("true");
    //保存历史数据级别设置为full最高级别，便于历史数据的追溯
    standaloneProcessEngineConfiguration.setHistoryLevel(HistoryLevel.FULL);
    //关闭activiti自动部署（使用流程设计器部署，不使用具体文件访问方式）
    standaloneProcessEngineConfiguration.setDbHistoryUsed(true);
    return standaloneProcessEngineConfiguration;
  }*/

  /**
   * 工作流流程引擎
   * @param processEngineConfiguration
   * @return
   */
  @Bean
  public ProcessEngine processEngine(ProcessEngineConfiguration processEngineConfiguration){
    return processEngineConfiguration.buildProcessEngine();
  }

  /**
   * RuntimeService
   * @param processEngine
   * @return
   */
  @Bean
  public RuntimeService runtimeService(ProcessEngine processEngine){
    return processEngine.getRuntimeService();
  }

  /**
   * TaskService
   * @param processEngine
   * @return
   */
  @Bean
  public TaskService taskService(ProcessEngine processEngine){
    return processEngine.getTaskService();
  }

  /**
   * RepositoryService
   * @param processEngine
   * @return
   */
  @Bean
  public RepositoryService repositoryService(ProcessEngine processEngine){
    return processEngine.getRepositoryService();
  }

  /**
   * HistoryService
   * @param processEngine
   * @return
   */
  @Bean
  public HistoryService historyService(ProcessEngine processEngine) {
    return processEngine.getHistoryService();
  }

}
