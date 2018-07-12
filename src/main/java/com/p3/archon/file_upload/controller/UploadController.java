package com.p3.archon.file_upload.controller;

import com.p3.archon.common.beans.ApplicationResponse;
import com.p3.archon.common.exception.StorageFileNotFoundException;
import com.p3.archon.common.services.StorageService;
import com.p3.archon.common.utils.MapBuilder;
import com.p3.archon.common.utils.XMLFileReader;
import com.p3.archon.file_upload.model.UploadModel;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.inst2xsd.Inst2Xsd;
import org.apache.xmlbeans.impl.inst2xsd.Inst2XsdOptions;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/files")
public class UploadController {

	private final StorageService storageService;
	private final Logger logger = LoggerFactory.getLogger(UploadController.class);

	/**
	 * @param storageService
	 */
	@Autowired
	public UploadController(StorageService storageService) {
		this.storageService = storageService;
	}

	/**
	 * @param model
	 * @return
	 * @throws IOException
	 */
	@GetMapping("/list")
	public String listUploadedFiles(Model model) throws IOException {
		model.addAttribute("files",
				storageService.loadAll()
						.map(path -> MvcUriComponentsBuilder
								.fromMethodName(UploadController.class, "serveFile", path.getFileName().toString())
								.build().toString())
						.collect(Collectors.toList()));
		return "uploadForm";
	}

	/**
	 * @param filename
	 * @return
	 */
	@GetMapping("/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

		Resource file = storageService.loadAsResource(filename);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}

	/**
	 * @param file
	 * @param redirectAttributes
	 * @return
	 */
	@PostMapping("/upload")
	public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

		storageService.store(file);
		redirectAttributes.addFlashAttribute("message",
				"You successfully uploaded " + file.getOriginalFilename() + "!");

		return "redirect:/";
	}

	/**
	 * @param exc
	 * @return
	 */
	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}

	public static HashMap<String, String> xsdFilePathMap = new HashMap<>();

	/** Save the uploaded file to this folder */
	private static String UPLOADED_FOLDER;
	private static String JSON_UPLOADED_FOLDER;

	@GetMapping
	public ApplicationResponse index() {
		return ApplicationResponse.success("Application is running!!");
	}

	@CrossOrigin()
	@PostMapping("/single")
	public ApplicationResponse singleFileUpload(@RequestParam("file") MultipartFile file) {

		logger.debug("Single file upload! ");
		JSON_UPLOADED_FOLDER = System.getProperty("user.dir");

		if (StringUtils.isEmpty(file)) {
			return ApplicationResponse.failure("Please select a file!");
		}

		try {
			byte[] bytes = file.getBytes();
			Path path = Paths.get(JSON_UPLOADED_FOLDER + File.separator + file.getOriginalFilename());
			Files.write(path, bytes);
		} catch (IOException e) {
			return ApplicationResponse.failure(e.getMessage());
		}

		return ApplicationResponse.success("You successfully uploaded '" + file.getOriginalFilename() + "'");
	}

	@CrossOrigin()
	@PostMapping("/multi")
	public ApplicationResponse uploadFileMulti(
			@RequestParam(value = "name", required = false, defaultValue = "uploadedfiles") String name,
			@RequestParam("files") MultipartFile[] uploadfiles) {

		logger.debug("Multiple file upload!");
		// Get file name
		String uploadedFileName = Arrays.stream(uploadfiles).map(x -> x.getOriginalFilename())
				.filter(x -> !StringUtils.isEmpty(x)).collect(Collectors.joining(" , "));

		if (StringUtils.isEmpty(uploadedFileName)) {
			return ApplicationResponse.failure("Please select a file!");
		}

		UploadModel model = new UploadModel();

		UUID id;
		if (name == null) {
			id = UUID.randomUUID();
			name = id.toString().substring(0, 8);
			model.setName(name);
		}

		try {
			List<String> filesPath = saveUploadedFiles(Arrays.asList(uploadfiles), name);
			model.setFilesPath(filesPath);
			getXsdConversionFiles(filesPath);
		} catch (IOException e) {
			return ApplicationResponse.failure(e.getMessage());
		}
		// treeStructureCreation();
		return ApplicationResponse.success(MapBuilder.of("files", model));
	}

	/** Save the uploaded file(s) */
	private List<String> saveUploadedFiles(List<MultipartFile> files, String name) throws IOException {

		logger.debug("Multiple file upload! With UploadModel");

		List<String> filesPath = new ArrayList<>();
		String currentDirectory = System.getProperty("user.dir");
		new File(currentDirectory + File.separator + name).mkdir();
		UPLOADED_FOLDER = currentDirectory + File.separator + name + File.separator;

		for (MultipartFile file : files) {
			if (file.isEmpty()) {
				continue;
			}

			try {
				byte[] bytes = file.getBytes();
				Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
				Files.write(path, bytes);
				filesPath.add(UPLOADED_FOLDER + file.getOriginalFilename());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return filesPath;
	}

	private void getXsdConversionFiles(List<String> filesPath) throws IOException {
		// String currentDirectory = System.getProperty("user.dir");
		String location = UPLOADED_FOLDER;
		// currentDirectory + File.separator + System.currentTimeMillis();
		for (String fileName : filesPath) {
			final Inst2XsdOptions options = new Inst2XsdOptions();
			options.setDesign(Inst2XsdOptions.DESIGN_RUSSIAN_DOLL);
			XmlObject[] xml = null;
			try {
				xml = new XmlObject[] { XmlObject.Factory.parse(new File(fileName)) };
			} catch (XmlException e) {
				e.printStackTrace();
			}
			final SchemaDocument[] schemaDocs = Inst2Xsd.inst2xsd(xml, options);
			getSchema(schemaDocs[0], fileName, location);
		}
	}

	private void getSchema(SchemaDocument schemaDocument, String fileName, String location) throws IOException {
		StringWriter writer = new StringWriter();
		schemaDocument.save(writer, new XmlOptions().setSavePrettyPrint());
		writer.close();

		File f = new File(fileName);
		new File(location).mkdir();
		String xsdFile = location + File.separator + f.getName().substring(0, f.getName().indexOf(".")) + ".xsd";
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(xsdFile));
		bufferedWriter.write(writer.toString());
		bufferedWriter.close();

		xsdFilePathMap.put(f.getName(), xsdFile);
	}

	@SuppressWarnings("unused")
	private void treeStructureCreation() {
		xsdFilePathMap.forEach((k, v) -> {
			System.out.println("file : " + k + " value : " + v);
			XMLFileReader xmlFileReader = new XMLFileReader(xsdFilePathMap.get(k));
			xmlFileReader.readXml();
		});
	}
}
