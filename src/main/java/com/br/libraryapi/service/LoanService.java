package com.br.libraryapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.br.libraryapi.api.dto.LoanFilterDTO;
import com.br.libraryapi.model.entity.Book;
import com.br.libraryapi.model.entity.Loan;

public interface LoanService {
	
	Loan save(Loan loan);
	
	Optional<Loan> getById(Long id);

	Loan update(Loan loan);
	
	Page<Loan> find(LoanFilterDTO filter, org.springframework.data.domain.Pageable pageable);

	Page<Loan> getLoansByBook(Book book, Pageable pageable);

	List<Loan> getAllLateLoan();
}
