package com.p3.archon.xsd_generation;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author omjigupta
 */
@Setter
@Getter
public class Result {
  Long id;
  String name;
  List<Children> children = new ArrayList<Children>();

  @Override
  public String toString() {
    return "Result{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", children=" + children +
            '}';
  }
}
