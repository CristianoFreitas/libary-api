package com.br.libraryapi.api.service.impl;

import org.springframework.stereotype.Service;

import com.br.libraryapi.api.model.entity.Book;
import com.br.libraryapi.api.model.repository.BookRepository;
import com.br.libraryapi.api.service.BookService;
import com.br.libraryapi.exception.BusinessException;

@Service
public class BookServiceImpl implements BookService {

	private BookRepository repository;

	public BookServiceImpl(BookRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public Book save(Book book) {
		if (repository.existsByIsbn(book.getIsbn())) {
			throw new BusinessException("Isbn já cadastrado");
		}
		return repository.save(book);
	}

}
