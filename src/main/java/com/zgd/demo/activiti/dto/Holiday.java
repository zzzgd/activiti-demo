package com.zgd.demo.activiti.dto;

import java.io.Serializable;

/**
 * Holiday
 *
 * @author zgd
 * @date 2021/8/2 15:05
 */
public class Holiday implements Serializable {

  private Integer id;

  private Integer days;


  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getDays() {
    return days;
  }

  public void setDays(Integer days) {
    this.days = days;
  }
}
