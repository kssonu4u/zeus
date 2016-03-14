package com.zeus.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.zeus.dao.BuildDetailsRepository;
import com.zeus.model.BuildDetails;
import com.zeus.service.FilesService;
import com.zeus.util.FileAndDirectoryUtil;

@Service
public class FilesServiceImpl implements FilesService{
	
	@Value("${base.directory.path}")
	private String baseDirectoryPath;
	private String DATE_FORMAT = "EEE, d MMM yyyy HH:mm:ss z";
	
	@Override
	public List<JSONObject> getFilesAndAttributes(String path) {
		if(path == null || path.length() <1){
			throw new NullPointerException("Empty Path");
		}
		path = baseDirectoryPath + path;
		try{
			File dir = new File(path);
			File[] fileList = dir.listFiles();
	
			List<JSONObject> resultList = new ArrayList<JSONObject>();
			SimpleDateFormat dt = new SimpleDateFormat(DATE_FORMAT);
			if (fileList != null) {
				for (File f : fileList) {
					if (!f.exists()) {
						continue;
					}
					JSONObject result = new JSONObject();
					BasicFileAttributes attrs = Files.readAttributes(f.toPath(), BasicFileAttributes.class);
					result.put("name", f.getName());
					result.put("date", dt.format(new Date(attrs.lastModifiedTime().toMillis())));
					result.put("size", FileUtils.byteCountToDisplaySize(f.length()));
					if(f.isDirectory()){
						File subDir = new File(path + "/" + f.getName());
						result.put("contentCount", subDir.listFiles().length);
					}
					result.put("path", f.getParent().replaceFirst(baseDirectoryPath, ""));
					result.put("type", f.isFile() ? "file" : "dir");
					
					resultList.add(result);
				}
			}
			return resultList;
		}catch(Exception e){
			
		}
		return null;
	}

	@Override
	public void moveFile(String source, String destination) throws IOException {
		FileAndDirectoryUtil fileAndDirectoryUtil = new FileAndDirectoryUtil();
		fileAndDirectoryUtil.moveFile(baseDirectoryPath + source, baseDirectoryPath + destination);
		
	}

	@Override
	public List<BuildDetails> getBuildHistory(String file, String path) throws Exception {
		return buildDetailsRepository.findByNameAndPath(file, path);
	}
	
	@Autowired
	BuildDetailsRepository buildDetailsRepository;
	private static final Logger logger = LogManager.getLogger(FilesService.class);
	
	
}
