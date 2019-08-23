package com.p3.archon.xsd_generation.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.p3.archon.common.services.StorageService;
import com.p3.archon.xsd_generation.services.PdiSchemaService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/pdigen")
public class XsdController {

	private final StorageService storageService;
	private final Logger logger = LoggerFactory.getLogger(XsdController.class);

	@Autowired
	/**
	 * 
	 */
	public XsdController(StorageService storageService) {
		this.storageService = storageService;
	}

	@Autowired
	PdiSchemaService pdiSchemaService;

	@CrossOrigin()
	@PostMapping("/upload")
	public StreamingResponseBody getPdiSchemaFile(@RequestParam("file") MultipartFile file) throws IOException {
		storageService.store(file);
		String currentDirectory = System.getProperty("user.dir");

		pdiSchemaService.generator(storageService.load(file.getOriginalFilename()).toString());

		String fileName = "pdi-schema.xsd";

		InputStream inputStream = new FileInputStream(new File(currentDirectory + File.separator + fileName));

		return outputStream -> {
			int nRead;
			byte[] data = new byte[1024];
			while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
				logger.info("Writing some bytes..");
				outputStream.write(data, 0, nRead);
			}
		};
	}
}
