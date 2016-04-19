package com.zeus.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystemException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.zeus.model.BuildDetails;
import com.zeus.service.FilesService;

@RestController
public class FilesController {
	
	@Value("${base.directory.path}")
	private String baseDirectoryPath;

	private static final Logger logger = LogManager.getLogger(FilesController.class);
   
	@RequestMapping(value="/files", method = RequestMethod.GET)
	public @ResponseBody String listFiles(@RequestParam(value="directory", required = true) String directory) throws FileSystemException{
		try {
			List<JSONObject> resultList = filesService.getFilesAndAttributes(directory);
			return resultList.toString();
		} catch (Exception e) {
			logger.error(String.format("Error in getting directory data. Directory = %s, Exception message = %s", directory, e.getMessage()) , e);
			throw new FileSystemException("Unable to list files. Try Again.");
		}
	}
	
	@RequestMapping(value="/build_history", method = RequestMethod.GET)
	public @ResponseBody List<BuildDetails> buildHistory(@RequestParam(value="file", required = true) String file, @RequestParam(value="path", required = true) String path) throws Exception{
		try{
			return filesService.getBuildHistory(file, path);
		}catch(Exception e){
			logger.error(String.format("Error in getting build history. File : %s, Path : %s", file, path), e);
			throw e;
		}
		
	}
	
	@RequestMapping(value="/build", method = RequestMethod.POST)
	public @ResponseBody String build(@RequestBody BuildDetails buildDetails) throws Exception{
		try{
			StringBuilder cmd = new StringBuilder(" nohup java -jar ");
			cmd.append(baseDirectoryPath + buildDetails.getPath() + "/" + buildDetails.getName());
			if(!"development".equalsIgnoreCase(buildDetails.getEnvironment())){
				cmd.append(" --spring.profiles.active=" + buildDetails.getEnvironment());
			}
			Process processes = Runtime.getRuntime().exec(cmd.append(" &").toString());
			filesService.saveBuildHistory(buildDetails);
			System.out.println(buildDetails);
		}catch(Exception e){
			logger.error(String.format("Error in building. File : %s, Path : %s", buildDetails.getName(), buildDetails.getPath()), e);
			throw e;
		}
		return null;
		
	}
	
	@RequestMapping(value="/show_java_processes", method = RequestMethod.GET)
	public @ResponseBody String showJavaProcesses() throws IOException{
		try{
			Process processes = Runtime.getRuntime().exec("ps o user=,pid=,stime=,cmd= -C java");
			System.out.println(processes);
			String process;
			List<JSONObject> processDetails = new ArrayList<JSONObject>();
			BufferedReader input = new BufferedReader(new InputStreamReader(processes.getInputStream()));
			while ((process = input.readLine()) != null) {
				if(process.contains(".jar") || process.contains(".war")){
					JSONObject tempData = new JSONObject();
					String[] param = process.trim().replaceAll("\\s+", " ").split(" ");
					tempData.put("user", param[0]);
					tempData.put("pid", param[1]);
					tempData.put("stime", param[2]);
					for(int i=3;i<param.length;i++){
						if(param[i].contains(".war") || param[i].contains(".jar")){
							tempData.put("name", param[i]);
							break;
						}
					}
					processDetails.add(tempData);
				}
			}
			
			input.close();
			return processDetails.toString();
		}catch(Exception e){
			logger.error(String.format("Error in getting process data. Exception message = %s", e.getMessage()) , e);
			throw e;
		}
	}
	
	@RequestMapping(value="kill_process/{pid}", method=RequestMethod.GET)
	public void killProcess(@PathVariable(value="pid") String pid) throws IOException{
		try{
			Process p = Runtime.getRuntime().exec("ps -p " + pid);
			if(p != null){
				Runtime.getRuntime().exec("kill -9 " + pid);
			}
		}catch(Exception e){
			logger.error(String.format("Error in killing process. Pid = %s, Exception message = %s", pid, e.getMessage()) , e);
			throw e;
		}
		
	}
	
	@RequestMapping(value="/download", method = RequestMethod.GET)
	public void download(@RequestParam(value="file", required = true) String file, 
			                                         @RequestParam(value="path", required = true) String path,  
			                                         HttpServletResponse response) throws Exception{
		try{
			 filesService.download(file, path, response);
		}catch(Exception e){
			logger.error(String.format("Error in downloading. File : %s, Path : %s", file, path), e);
			throw e;
		}
		
	}
	
	@RequestMapping(value="/delete", method = RequestMethod.DELETE)
	public void deleteResource(@RequestParam(value="file", required = true) String file, @RequestParam(value="path", required = true) String path) throws Exception{
		try{
			 filesService.deleteResource(file, path);
		}catch(Exception e){
			logger.error(String.format("Error in deleting resource. File : %s, Path : %s", file, path), e);
			throw e;
		}
		
	}
	
	@RequestMapping(value="/move_resource", method = RequestMethod.POST)
	public void moveResource(@RequestParam(value="file", required = true) String file, @RequestParam(value="path", required = true) String path) throws Exception{
		try{
			 filesService.moveFileToArchive(file, path);
		}catch(Exception e){
			logger.error(String.format("Error in deleting resource. File : %s, Path : %s", file, path), e);
			throw e;
		}
		
	}
	
	@Autowired
	FilesService filesService;
	
}
