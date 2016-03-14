package com.zeus.util;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InitProject {
	@Value("${base.directory.path}")
	private String baseDirectoryPath;
	
	private static final String ARCHIVE = "archive"; 
	private static final String LIVE = "live";
	
	private static final Logger logger = LogManager.getLogger(InitProject.class);
	
	@PostConstruct
	private void initialize() throws Exception{
		logger.info("************* INITIALIZING SYSTEM *************");
		try{
			FileAndDirectoryUtil fileAndDirectoryUtil = new FileAndDirectoryUtil();
			logger.info("Step 1 : Creating builder....");
			fileAndDirectoryUtil.addFolder(baseDirectoryPath);
			logger.info("builder created.");
			
			logger.info("Step 2 : Creating archive....");
			fileAndDirectoryUtil.addFolder(baseDirectoryPath + "/" + ARCHIVE);
			logger.info("archive created.");
			
			logger.info("Step 3 : Creating live....");
			fileAndDirectoryUtil.addFolder(baseDirectoryPath + "/" + LIVE);
			logger.info("live created.");
			
			logger.info("************* SYSTEM INITIALIZED *************");
		}catch(Exception e){
			logger.error("Directory Structure creation failed. Unable to initialize application.", e);
			throw new RuntimeException("Directory Structure creation failed. Unable to initialize application.");
		}
	}
}
