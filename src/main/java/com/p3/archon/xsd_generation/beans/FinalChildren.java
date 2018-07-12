package com.p3.archon.xsd_generation.beans;

import lombok.Getter;
import lombok.Setter;

/***
 * @author omjigupta
 */
@Setter
@Getter
public class FinalChildren {
  String frompath;
  String topath;
  String datatype;
  String name;
  boolean search;
  boolean result;
  Long id;
  String filename;
  String minoccurance;
  String maxoccurance;

  @Override
  public String toString() {
    return "FinalChildren{" +
            "frompath='" + frompath + '\'' +
            ", topath='" + topath + '\'' +
            ", datatype='" + datatype + '\'' +
            ", name='" + name + '\'' +
            ", search=" + search +
            ", result=" + result +
            ", id=" + id +
            ", filename='" + filename + '\'' +
            ", minoccurance='" + minoccurance + '\'' +
            ", maxoccurance='" + maxoccurance + '\'' +
            '}';
  }
}
