package com.p3.archon.file_upload.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

@RestController
@RequestMapping("/api/download")
public class DownloadController {

  private final Logger logger = LoggerFactory.getLogger(DownloadController.class);

  @GetMapping("/files/{filename}")
  public StreamingResponseBody getSteamingFile(@PathVariable("filename") String fileName, HttpServletResponse response) throws IOException {

    String currentDirectory = System.getProperty("user.dir");

    //get the mimetype
    String mimeType = URLConnection.guessContentTypeFromName(fileName);
    if (mimeType == null) {
      //unknown mimetype so set the mimetype to application/octet-stream
      mimeType = "application/octet-stream";
    }

    response.setContentType(mimeType);
    response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

    InputStream inputStream = new FileInputStream(new File(currentDirectory + File.separator + "files_download" + File.separator+ fileName));

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
