package com.zeus.exception;

import org.apache.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@ControllerAdvice
public class GlobalExceptionHandler {
	private Logger logger = Logger.getLogger(GlobalExceptionHandler.class);

	  @ResponseStatus(value=HttpStatus.CONFLICT) 
	  @ExceptionHandler(DataIntegrityViolationException.class)
	  @ResponseBody
	  public ErrorInfo conflictHandler(Exception exception) {
		  printError(exception);
		  return new ErrorInfo(exception.getMessage());
	  }
	  
	  @ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR) 
	  @ExceptionHandler(Exception.class)
	  @ResponseBody
	  public ErrorInfo exceptionHandler(Exception exception) {
		  printError(exception);
		  return new ErrorInfo(exception.getMessage());
	  }
	  
	  private void printError(Exception exception) {
		  logger.error(exception.getMessage());
		  exception.printStackTrace();
	  }
}
