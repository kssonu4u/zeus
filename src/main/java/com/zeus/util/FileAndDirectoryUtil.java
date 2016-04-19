package com.zeus.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

import org.springframework.util.FileSystemUtils;

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
	
	public void moveFile(String source, String destination) throws IOException{
		File src = new File(source);
		if(!src.exists()){
			throw new FileNotFoundException("File not found.");
		}
		
		File dest = new File(destination);
		src.renameTo(dest);
		
	}
	
	public void deleteResource(String filePath) throws FileNotFoundException{
		File file = new File(filePath);
		if(!file.exists()){
			throw new FileNotFoundException(filePath);
		}
		
		FileSystemUtils.deleteRecursively(file);
	}
	
	
}
