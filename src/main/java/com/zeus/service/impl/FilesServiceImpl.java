package com.zeus.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

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
	private static final String ARCHIVE = "archive"; 
	private static final String LIVE = "live";
	private static final String BUILDER = "builder";
	
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
	public void moveFileToArchive(String file, String path) throws Exception {
		FileAndDirectoryUtil fileAndDirectoryUtil = new FileAndDirectoryUtil();
		if(path.contains(ARCHIVE)){
			throw new Exception(file + " already is in archive.");
		}
		fileAndDirectoryUtil.moveFile(baseDirectoryPath + path + "/" + file, baseDirectoryPath + BUILDER + "/" + ARCHIVE + "/" + file);
		
	}

	@Override
	public List<BuildDetails> getBuildHistory(String file, String path) throws Exception {
		return buildDetailsRepository.findByNameAndPathOrderByUpdatedAtDesc(file, path);
	}
	
	@Override
	public void saveBuildHistory(BuildDetails buildDetails){
		if(buildDetails != null)
			buildDetailsRepository.save(buildDetails);
	}
	
	@Autowired
	BuildDetailsRepository buildDetailsRepository;
	private static final Logger logger = LogManager.getLogger(FilesService.class);

	@Override
	public void download(String file, String path, HttpServletResponse httpServletResponse) throws Exception {
		httpServletResponse.setContentType("application/octet-stream;charset=utf-8");
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename= \""+file +"\"");
        httpServletResponse.setHeader(headerKey, headerValue);
        try {
            OutputStream output = httpServletResponse.getOutputStream();
            FileInputStream fileIn = new FileInputStream(baseDirectoryPath + path + "/" + file);
            byte[] outputByte = new byte[4096];
              while(fileIn.read(outputByte, 0, 4096) != -1)
              {
               	output.write(outputByte, 0, 4096);
              }
            output.close();
            fileIn.close();
        } catch (IOException e) {
            throw new Exception("Error in downloading file. " + e.getMessage());
        }		
	}
	
	@Override
	public void deleteResource(String file, String path) throws FileNotFoundException{
		FileAndDirectoryUtil fileAndDirectoryUtil = new FileAndDirectoryUtil();
		fileAndDirectoryUtil.deleteResource(baseDirectoryPath + path + "/" + file);
	}
	
	
}
