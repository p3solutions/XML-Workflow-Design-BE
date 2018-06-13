package com.p3.archon.file_upload.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class UploadModel {

  private String name;

  private MultipartFile[] files;
}
