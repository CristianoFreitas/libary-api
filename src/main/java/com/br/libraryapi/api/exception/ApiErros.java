package com.br.libraryapi.api.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.validation.BindingResult;

import com.br.libraryapi.exception.BusinessException;

public class ApiErros {

	private List<String> errors;

	public ApiErros(BindingResult bindingResult) {
		this.errors = new ArrayList<String>();
		bindingResult.getAllErrors().forEach(  error -> this.errors.add(error.getDefaultMessage()));
	}
	
	public ApiErros(BusinessException ex) {
		this.errors = Arrays.asList(ex.getMessage());
	}

	public List<String> getErrors() {
		return errors;
	}
	
}
