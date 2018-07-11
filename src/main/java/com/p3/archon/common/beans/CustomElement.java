/**
 * 
 */
package com.p3.archon.common.beans;

import org.dom4j.Node;
import org.dom4j.tree.DefaultElement;

import lombok.Getter;
import lombok.Setter;

/**
 * @author saideepak
 *
 */
@Getter
@Setter
public class CustomElement extends DefaultElement{
	
	public CustomElement(Node element) {
		super(element.getName());
	}

	private boolean attribute;
}
