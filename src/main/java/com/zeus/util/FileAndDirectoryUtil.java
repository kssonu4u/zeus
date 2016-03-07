package com.zeus.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

import org.junit.runners.model.InitializationError;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

public class FileAndDirectoryUtil {
	
	public void addFolder(String folderPath) throws IOException{
		File newFolder = new File(folderPath);
		
		if(!newFolder.exists() && !newFolder.mkdirs()){
			throw new FileAlreadyExistsException(folderPath);
		}
	}
	
	public File addFile(String filePath) throws IOException{
		File newFile = new File(filePath);
		if(newFile.exists())
			return newFile;
		
		newFile.createNewFile();
		return newFile;
	}
	
	
	
}
