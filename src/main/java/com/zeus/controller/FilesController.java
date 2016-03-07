package com.zeus.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FilesController {
	
	@Value("${base.directory.path}")
	private String baseDirectoryPath;
	
	private static final String ARCHIVE = "archive"; 
	private static final String LIVE = "live";
	private static final Logger logger = LogManager.getLogger(FilesController.class);
	private String DATE_FORMAT = "EEE, d MMM yyyy HH:mm:ss z";
   
	@RequestMapping(value="/directory/{directory}/files", method = RequestMethod.GET)
	public @ResponseBody String listFiles(@PathVariable("directory") String directory){
		try {
			File dir = new File(baseDirectoryPath + "/" + directory);
			File[] fileList = dir.listFiles();

			List<JSONObject> resultList = new ArrayList<JSONObject>();
			SimpleDateFormat dt = new SimpleDateFormat(DATE_FORMAT);
			if (fileList != null) {
				// Calendar cal = Calendar.getInstance();
				for (File f : fileList) {
					if (!f.exists()) {
						continue;
					}
					BasicFileAttributes attrs = Files.readAttributes(f.toPath(), BasicFileAttributes.class);
					JSONObject el = new JSONObject();
					el.put("name", f.getName());
					el.put("date", dt.format(new Date(attrs.lastModifiedTime().toMillis())));
					el.put("size", f.length());
					el.put("type", f.isFile() ? "file" : "dir");
					resultList.add(el);
				}
			}

			return new JSONObject().put("result", resultList).toString();
		} catch (Exception e) {
			logger.error("list", e);
		}
		return null;
	}
	
}
