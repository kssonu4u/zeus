package com.zeus.util;

import java.io.File;
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
		File dest = new File(destination);
		FileSystemUtils.copyRecursively(src, dest);
		FileSystemUtils.deleteRecursively(src);
	}
	
	
}
