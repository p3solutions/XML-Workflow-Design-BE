package com.p3.archon.xsd_generation;

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
            '}';
  }
}
