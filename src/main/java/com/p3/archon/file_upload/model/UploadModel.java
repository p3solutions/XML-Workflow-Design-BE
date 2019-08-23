package com.p3.archon.file_upload.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Data
public class UploadModel {

  private String name;

  @JsonIgnore private MultipartFile[] files;

  private List<String> filesPath;
}
