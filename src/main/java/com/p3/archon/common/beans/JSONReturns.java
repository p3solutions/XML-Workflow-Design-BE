/**
 * 
 */
package com.p3.archon.common.beans;

import java.util.List;

import org.json.simple.JSONObject;

import lombok.Getter;
import lombok.Setter;

/**
 * This Class is a POJO class
 * 
 * @author saideepak
 */
@Getter
@Setter
public class JSONReturns {
	private int id;
	private List<JSONObject> jsonList;
}
