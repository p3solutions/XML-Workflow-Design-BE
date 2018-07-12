package com.p3.archon.xsd_generation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/api")
public class XsdController {

  @Autowired
  PdiSchemaService pdiSchemaService;
  
  @CrossOrigin()
  @GetMapping("/files/schema/{file}")
  public StreamingResponseBody getPdiSchemaFile(@RequestParam("file") MultipartFile file) throws IOException {

    String currentDirectory = System.getProperty("user.dir");

    pdiSchemaService.generator(currentDirectory + File.separator + file.getOriginalFilename());

    String fileName = "pdi-schema.xsd";

    InputStream inputStream = new FileInputStream(new File(currentDirectory + File.separator+ fileName));

    return outputStream -> {
      int nRead;
      byte[] data = new byte[1024];
      while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
        System.out.println("Writing some bytes..");
        outputStream.write(data, 0, nRead);
      }
    };
  }
}
