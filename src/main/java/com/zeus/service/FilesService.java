package com.zeus.service;

import java.io.IOException;
import java.util.List;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.zeus.model.BuildDetails;

public interface FilesService {
	
	List<JSONObject> getFilesAndAttributes(String path);
    void moveFile(String source, String destination) throws IOException;
	List<BuildDetails> getBuildHistory(String file, String path) throws Exception;
	void saveBuildHistory(BuildDetails buildDetails);
}