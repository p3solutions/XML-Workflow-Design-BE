package com.p3.archon.xsd_generation.beans;

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
  String hasChildren;

  @Override
  public String toString() {
    return "Result{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", children=" + children +
            ", hasChildren='" + hasChildren + '\'' +
            '}';
  }
}
