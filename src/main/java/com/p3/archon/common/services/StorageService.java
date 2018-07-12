/**
 * 
 */
package com.p3.archon.common.services;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author saideepak
 *
 */
public interface StorageService {
	/**
	 * The init Method
	 */
	void init();

	/**
	 * Store file in a location
	 * @param file
	 */
	void store(MultipartFile file);
	
	/**
	 * Store file in a location
	 * @param file
	 */
	void store(MultipartFile[] file);

	/**
	 * Load All files from a location 
	 * @return {@link Stream}
	 */
	Stream<Path> loadAll();

	/**
	 * Load one file from a location
	 * @param fileName
	 * @return {@link Path}
	 */
	Path load(String fileName);

	/**Load file as a {@link Resource} object
	 * @param fileName
	 * @return {@link Resource}
	 */
	Resource loadAsResource(String fileName);

	void deleteAll();
}
