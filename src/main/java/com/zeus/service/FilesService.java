package com.zeus.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.zeus.model.BuildDetails;

public interface FilesService {
	
	List<JSONObject> getFilesAndAttributes(String path);
    void moveFileToArchive(String source, String destination) throws Exception;
	List<BuildDetails> getBuildHistory(String file, String path) throws Exception;
	void saveBuildHistory(BuildDetails buildDetails);
	void download(String file, String path, HttpServletResponse response) throws Exception;
	void deleteResource(String file, String path) throws FileNotFoundException;
}