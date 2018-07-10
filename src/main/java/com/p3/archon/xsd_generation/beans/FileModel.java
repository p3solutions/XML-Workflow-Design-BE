package com.p3.archon.xsd_generation.beans;

import lombok.Getter;
import lombok.Setter;

/**
 * @author omjigupta
 */
@Setter
@Getter
public class FileModel {
  Result result;

  @Override
  public String toString() {
    return "FileModel{" +
            "result=" + result +
            '}';
  }
}
