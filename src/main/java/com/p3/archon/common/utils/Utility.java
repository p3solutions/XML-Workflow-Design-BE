/**
 * 
 */
package com.p3.archon.common.utils;

/**
 * @author saideepak
 *
 */
public class Utility {

	/**
	 * Converting URL with Back slashes(\) to Forward slashes(/)
	 * 
	 * @param file
	 * @return
	 */
	public String changeURLIfNeeded(String file) {
		if (file.contains("\\")) {
			file = file.replaceAll("\\\\+", "/");
		}
		return file;
	}
}
