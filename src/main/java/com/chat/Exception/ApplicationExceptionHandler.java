package com.chat.Exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApplicationExceptionHandler {

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(NotFoundException.class)

	public Map<String, String> handleBusinessException(NotFoundException e) {
		Map<String, String> errorMap = new HashMap<>();
		errorMap.put("errorMessage", e.getMessage());
		return errorMap;
	}

}