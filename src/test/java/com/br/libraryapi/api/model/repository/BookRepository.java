package com.br.libraryapi.api.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.br.libraryapi.api.model.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
	boolean existsByIsbn(String isbn);
}