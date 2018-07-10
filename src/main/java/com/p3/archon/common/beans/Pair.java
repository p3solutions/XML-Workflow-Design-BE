package com.p3.archon.common.beans;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Pair {
  private String name;
  private String value;

  public Pair(String name, String value) {
    this.name = name;
    this.value = value;
  }
}
