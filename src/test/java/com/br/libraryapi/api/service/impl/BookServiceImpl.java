package com.br.libraryapi.api.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.br.libraryapi.exception.BusinessException;
import com.br.libraryapi.model.entity.Book;
import com.br.libraryapi.model.repository.BookRepository;
import com.br.libraryapi.service.BookService;

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
	
	@Override
	public Optional<Book> getById(Long id) {
		return repository.findById(id);
	}

	@Override
	public void delete(Book book) {
		if (book == null || book.getId() == null) {
			throw new IllegalArgumentException("O Id do Livro não pode ser nulo");
		}
		this.repository.delete(book);		
	}

	@Override
	public Book update(Book book) {
		if (book == null || book.getId() == null) {
			throw new IllegalArgumentException("O Id do Livro não pode ser nulo");
		}
		return this.repository.save(book);
	}
	
	@Override
	public Page<Book> find(Book filter, Pageable pageRequest) {
		Example example = Example.of(filter,
								ExampleMatcher
									.matching()
									.withIgnoreCase()
									.withIgnoreNullValues()
									.withStringMatcher( StringMatcher.CONTAINING ));
		return repository.findAll(example, pageRequest);
	}

	@Override
	public Optional<Book> getBookByIsbn(String isbn) {
		return null;
	}
}
