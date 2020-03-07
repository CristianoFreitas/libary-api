package com.br.libraryapi.api.resources;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.br.libraryapi.api.dto.BookDTO;
import com.br.libraryapi.api.exception.ApiErros;
import com.br.libraryapi.api.model.entity.Book;
import com.br.libraryapi.api.service.BookService;
import com.br.libraryapi.exception.BusinessException;

@RestController
@RequestMapping("/api/books")
public class BookController {
	
	private BookService service;
	private ModelMapper modelMapper;
	
	public BookController(BookService service, ModelMapper modelMapper) {
		super();
		this.service = service;
		this.modelMapper = modelMapper;
	}


	@PostMapping
	@ResponseStatus(value = HttpStatus.CREATED)
	public BookDTO create( @RequestBody @Valid BookDTO dto) {
		Book entity = modelMapper.map( dto, Book.class);
		entity = service.save(entity);
		return modelMapper.map( entity, BookDTO.class);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public ApiErros handleValidationExceptions(MethodArgumentNotValidException ex) {
		BindingResult bindingResult = ex.getBindingResult();
		return new ApiErros(bindingResult);
	 }
	
	
	@ExceptionHandler(BusinessException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public ApiErros handleBusinessException(BusinessException ex) {
		return new ApiErros(ex);
	}
}
