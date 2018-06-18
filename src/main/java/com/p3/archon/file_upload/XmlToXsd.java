/**
 * 
 */
package com.p3.archon.file_upload;

import com.thaiopensource.relaxng.translate.Driver;

/**
 * @author saideepak
 *
 */
public class XmlToXsd {

	public void convertXmltoXsd(String xml, String xsd) {
		String[] arguments = new String[] { xml, xsd };

		Driver.main(arguments);

	}

}
