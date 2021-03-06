package com.devsuperior.dscatalog.resources.exceptions;

import java.time.Instant;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.devsuperior.dscatalog.services.exceptions.DatabaseIntegrityException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

//Classe que vai interceptar exceções e tratá-las
//Para isso, usa-se a annotation ControllerAdvice
@ControllerAdvice
public class ResourceExceptionHandler {
	
	//Para tratar excecoes, a annotation recebe um argumento .class da excecao
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<StandardError> entityNotFound(ResourceNotFoundException e, HttpServletRequest request){
		StandardError error = new StandardError();
		error.setTimestamp(Instant.now());
		//Classe que tem os codigos http
		error.setStatus(HttpStatus.NOT_FOUND.value());
		error.setError("Resource not found");
		error.setMessage(e.getMessage());
		error.setPath(request.getRequestURI());
		return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(error);
	}
	
	@ExceptionHandler(DatabaseIntegrityException.class)
	public ResponseEntity<StandardError> databaseInegrityViolated(DatabaseIntegrityException e, HttpServletRequest request){
		HttpStatus status = HttpStatus.BAD_REQUEST;
		StandardError error = new StandardError();
		error.setTimestamp(Instant.now());
		error.setStatus(status.value());
		error.setError("Database integrity violated");
		error.setMessage(e.getMessage());
		error.setPath(request.getRequestURI());
		return ResponseEntity.status(status.value()).body(error);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ValidationError> validationError(MethodArgumentNotValidException e, HttpServletRequest request){
		HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
		ValidationError error = new ValidationError();
		error.setTimestamp(Instant.now());
		error.setStatus(status.value());
		error.setError("Error Validation Exception");
		error.setMessage(e.getMessage());
		error.setPath(request.getRequestURI());
		
		e.getBindingResult().getFieldErrors().forEach(x -> error.addError(x.getField(), x.getDefaultMessage()));
		
		return ResponseEntity.status(status.value()).body(error);
	}
}
