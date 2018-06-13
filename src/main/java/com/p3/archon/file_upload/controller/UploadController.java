package com.p3.archon.file_upload.controller;

import com.p3.archon.common.beans.ApplicationResponse;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

  private final Logger logger = LoggerFactory.getLogger(UploadController.class);

  /** Save the uploaded file to this folder */
  private static String UPLOADED_FOLDER;

  @GetMapping
  public ApplicationResponse index() {
    String currentDirectory;
    //currentDirectory = System.getProperty("user.dir");
    File file = new File(".");
    currentDirectory = file.getAbsolutePath();
    System.out.println("Current working directory : "+currentDirectory);
    return ApplicationResponse.success("upload_file");
  }


  @PostMapping("/multi")
  public ApplicationResponse uploadFileMulti(
          @RequestParam("name") String name,
          @RequestParam("files") MultipartFile[] uploadfiles) {

    logger.debug("Multiple file upload!");

    // Get file name
    String uploadedFileName = Arrays.stream(uploadfiles).map(x -> x.getOriginalFilename())
            .filter(x -> !StringUtils.isEmpty(x)).collect(Collectors.joining(" , "));

    if (StringUtils.isEmpty(uploadedFileName)) {
      return ApplicationResponse.success("please select a file!");
    }

    try {
      saveUploadedFiles(Arrays.asList(uploadfiles), name);
    } catch (IOException e) {
      return ApplicationResponse.failure(e.getMessage());
    }

    return ApplicationResponse.success("Successfully uploaded!");

  }

  /** Save the uploaded file(s) */
  private void saveUploadedFiles(List<MultipartFile> files, String name) throws IOException {

    logger.debug("Multiple file upload! With UploadModel");

    String currentDirectory;
    currentDirectory = System.getProperty("user.dir");
    UPLOADED_FOLDER = currentDirectory;

    for (MultipartFile file : files) {

      if (file.isEmpty()) {
        continue;
      }

      byte[] bytes = file.getBytes();
      Path path = Paths.get(file.getOriginalFilename());
      //System.out.println(path);
      Files.write(path, bytes);
    }
  }
}
