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
public class Children {
  String frompath;
  String datatype;
  String name;
  boolean search;
  boolean result;
  Long id;
  String filename;
  List<Children> children = new ArrayList<Children>();
  String minoccurance;
  String maxoccurance;
  String hasChildren;
  String conditionvalue;
  String conditionpath;
  String condition;
  boolean iscondition;
  
  

  @Override
  public String toString() {
    return "Children{" +
            "frompath='" + frompath + '\'' +
            ", datatype='" + datatype + '\'' +
            ", name='" + name + '\'' +
            ", search=" + search +
            ", result=" + result +
            ", id=" + id +
            ", filename='" + filename + '\'' +
            ", children=" + children +
            ", minoccurance='" + minoccurance + '\'' +
            ", maxoccurance='" + maxoccurance + '\'' +
            ", hasChildren='" + hasChildren + '\'' +
            ", conditionvalue='" + conditionvalue + '\'' +
            ", conditionpath='" + conditionpath + '\'' +
            ", condition='" + condition + '\'' +
            ", iscondition='" + iscondition + '\'' +
            '}';
  }
}
