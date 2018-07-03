/**
 * 
 */
package com.p3.archon.common.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.p3.archon.common.exception.StorageException;
import com.p3.archon.common.exception.StorageFileNotFoundException;
import com.p3.archon.configurations.StorageProperties;

/**
 * @author saideepak
 *
 */
@Service
public class StorageServiceFileSystem implements StorageService {

	private final Path rootLocation;

	/**
	 * Constructor with Properties as argument
	 * 
	 * @param storageProperties
	 */
	@Autowired
	public StorageServiceFileSystem(StorageProperties storageProperties) {
		this.rootLocation = Paths.get(storageProperties.getLocation());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.p3.archon.common.services.StorageService#init()
	 */
	@Override
	public void init() {
		try {
			Files.createDirectory(rootLocation);
		} catch (IOException e) {
			throw new StorageException("Could not initialize Storage");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.p3.archon.common.services.StorageService#store(org.springframework.web.
	 * multipart.MultipartFile)
	 */
	@Override
	public void store(MultipartFile file) {
		try {
			if (file.isEmpty()) {
				throw new StorageFileNotFoundException("File is empty:" + file.getOriginalFilename());
			}
			Files.copy(file.getInputStream(), this.rootLocation.resolve(file.getOriginalFilename()));

		} catch (IOException e) {
			throw new StorageFileNotFoundException(
					"Unable to save file. Please Try again later:" + file.getOriginalFilename(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.p3.archon.common.services.StorageService#loadAll()
	 */
	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.rootLocation, 1).filter(path -> !path.equals(this.rootLocation))
					.map(path -> this.rootLocation.relativize(path));
		} catch (IOException e) {
			throw new StorageFileNotFoundException("Failed to load files", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.p3.archon.common.services.StorageService#load(java.lang.String)
	 */
	@Override
	public Path load(String fileName) {
		return rootLocation.resolve(fileName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.p3.archon.common.services.StorageService#loadAsResource(java.lang.String)
	 */
	@Override
	public Resource loadAsResource(String fileName) {
		try {
			Path path = load(fileName);
			Resource resource = new UrlResource(path.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new StorageFileNotFoundException("Could not read file:\" + fileName, e");
			}
		} catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file:" + fileName, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.p3.archon.common.services.StorageService#deleteAll()
	 */
	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
	}

}
