package com.p3.archon.tree_structure.Controller;

import com.p3.archon.common.beans.ApplicationResponse;
import com.p3.archon.common.utils.MapBuilder;
import com.p3.archon.common.utils.XMLFileReader;
import com.p3.archon.file_upload.controller.UploadController;
import com.p3.archon.tree_structure.model.TreeModel;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tree")
public class TreeController {

  @GetMapping("/files/{fileName}")
  @CrossOrigin
  public ApplicationResponse getTreeView(@PathVariable String fileName){
    TreeModel treeModel = new TreeModel();
    treeModel.setFileName(fileName);
    treeModel.setFileValue(treeStructureCreation(fileName));
    return ApplicationResponse.success(MapBuilder.of("treeview", treeModel));
  }

  private JSONObject treeStructureCreation(String fileName) {
    for (String key : UploadController.xsdFilePathMap.keySet()) {
      if(fileName.equals(key)){
        XMLFileReader xmlFileReader = new XMLFileReader(UploadController.xsdFilePathMap.get(key).replaceFirst(".xsd", ".xml"),UploadController.xsdFilePathMap.get(key));
        return xmlFileReader.readXml();
      }
    }
    return null;
  }
}
