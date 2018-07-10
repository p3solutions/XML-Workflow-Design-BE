package com.p3.archon.xsd_generation.beans;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author omjigupta
 */
@Setter
@Getter
public class FinalResult {
  List<FinalChildren> result;

  @Override
  public String toString() {
    return "FinalResult{" +
            "result=" + result +
            '}';
  }
}
