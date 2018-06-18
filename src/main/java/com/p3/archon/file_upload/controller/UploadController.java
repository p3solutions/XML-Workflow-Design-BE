package com.p3.archon.file_upload.controller;

import com.p3.archon.common.beans.ApplicationResponse;
import com.p3.archon.common.utils.MapBuilder;
import com.p3.archon.file_upload.model.UploadModel;
import lombok.NonNull;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.inst2xsd.Inst2Xsd;
import org.apache.xmlbeans.impl.inst2xsd.Inst2XsdOptions;
import org.apache.xmlbeans.impl.regex.ParseException;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

  private final Logger logger = LoggerFactory.getLogger(UploadController.class);

  /** Save the uploaded file to this folder */
  private static String UPLOADED_FOLDER; // ="/Users/omjigupta/Documents/testing/testing";

  @GetMapping
  public ApplicationResponse index() {
    return ApplicationResponse.success("Application is running!!");
  }

  @PostMapping("/multi")
  public ApplicationResponse uploadFileMulti(
      @RequestParam(value = "name", required = false) String name,
      @RequestParam("files") MultipartFile[] uploadfiles) {

    logger.debug("Multiple file upload!");

    // Get file name
    String uploadedFileName =
        Arrays.stream(uploadfiles)
            .map(x -> x.getOriginalFilename())
            .filter(x -> !StringUtils.isEmpty(x))
            .collect(Collectors.joining(" , "));

    if (StringUtils.isEmpty(uploadedFileName)) {
      return ApplicationResponse.success("Please select a file!");
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
    return ApplicationResponse.success(MapBuilder.of("files", model));

    //return ApplicationResponse.success("Successfully uploaded!");
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


  private void getXsdConversionFiles(List<String> filesPath) {
    for (String fileName : filesPath) {
      final Inst2XsdOptions options = new Inst2XsdOptions();
      options.setDesign(Inst2XsdOptions.DESIGN_RUSSIAN_DOLL);
      XmlObject[] xml = null;
      try {
        xml = new XmlObject[] {XmlObject.Factory.parse(new File(fileName))};
      } catch (XmlException e) {
        e.printStackTrace();
      } catch (ParseException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
      final SchemaDocument[] schemaDocs = Inst2Xsd.inst2xsd(xml, options);
      System.out.println(schemaDocs[0]);
    }

  }
}
