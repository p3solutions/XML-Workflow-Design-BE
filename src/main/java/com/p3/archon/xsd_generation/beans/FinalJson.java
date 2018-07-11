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
public class FinalJson {
  List<Children> children = new ArrayList<Children>();

  @Override
  public String toString() {
    return "FinalJson{" +
            "children=" + children +
            '}';
  }
}
