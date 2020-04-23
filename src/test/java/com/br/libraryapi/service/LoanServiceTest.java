package com.br.libraryapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.br.libraryapi.api.dto.LoanFilterDTO;
import com.br.libraryapi.exception.BusinessException;
import com.br.libraryapi.model.entity.Book;
import com.br.libraryapi.model.entity.Loan;
import com.br.libraryapi.model.repository.LoanRepository;
import com.br.libraryapi.service.impl.LoanServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {
	
	LoanService service;
	
	@MockBean
	LoanRepository repository;
	
	@BeforeEach
	public void setUp() {
		this.service = new LoanServiceImpl(repository);
	}

	@Test
	@DisplayName("Deve salvar um emprestimo")
	public void saveLoanTest() throws Exception {
		
		Loan savingLoan = createLoan();
		Book book = savingLoan.getBook();
		String customer = savingLoan.getCustomer();
		
		Loan savedLoan = Loan.builder()
				.id(1l)
				.book(book)
				.customer(customer)
				.loanDate(LocalDate.now())
				.build();
		
		when(repository.existsByBookAndNotReturned(book)).thenReturn(false);
		when(repository.save(savingLoan)).thenReturn(savedLoan);
		
		Loan loan = service.save(savingLoan);
		
		assertThat(loan.getId()).isEqualTo(savedLoan.getId());
		assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
		assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
		assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
	}
	
	@Test
	@DisplayName("Deve lançar erro de negócio ao salvar um empréstimo com um livro empréstimo já ")
	public void loanedBookSaveTest() throws Exception {
		
		Book book = Book.builder().id(1l).build();
		String customer = "Beltrano";
		
		Loan savingLoan = Loan.builder()
							.book(book)
							.customer(customer)
							.loanDate(LocalDate.now())
							.build();
		
		when(repository.existsByBookAndNotReturned(book)).thenReturn(true);
			
		Throwable exception = Assertions.catchThrowable( () ->  service.save(savingLoan));
		
		assertThat(exception)
				.isInstanceOf(BusinessException.class)
				.hasMessage("Book already loaned");
		
		Mockito.verify(repository, Mockito.never()).save(savingLoan);
	}

	@Test
	@DisplayName("Deve obter as informações de um empréstimo pelo ID")
	public void getLoanDetaisTest() {
		Long id = 1l;
		
		Loan loan = createLoan(); 
		loan.setId(id);
		
		when( repository.findById(id) ).thenReturn(Optional.of(loan));
		
		Optional<Loan> result = service.getById(id);
		
		assertThat(result.isPresent()).isTrue();
		assertThat(result.get().getId()).isEqualTo(id);
		assertThat(result.get().getId()).isEqualTo(id);
		assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
		assertThat(result.get().getBook()).isEqualTo(loan.getBook());
		assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());
		
		verify(repository).findById(id);
	}
	
	@Test
	@DisplayName("Deve atualizar um empréstimo")
	public void updateLoanTest() {
		Long id = 1l;
		
		Loan loan = createLoan();
		loan.setId(id);
		loan.setReturned(true);
		
		Mockito.when( repository.save(loan) ).thenReturn(loan);
		
		Loan updatedLoan = service.update(loan);
		
		assertThat(updatedLoan.getReturned()).isTrue();
		verify(repository).save(loan);
		
	}
	
	public static Loan createLoan() {
		Book book = Book.builder().id(1l).build();
		String customer = "Beltrano";
		
		return Loan.builder()
							.book(book)
							.customer(customer)
							.loanDate(LocalDate.now())
							.build();
	}
	
	@Test
	@DisplayName("Deve filtrar emprestimos pelas propriedades")
	public void findLoan() { 

		LoanFilterDTO loanFilterDto = LoanFilterDTO.builder().customer("Beltrano").isbn("321").build();
		
		Loan loan = createLoan();
		loan.setId(1l);
		
		PageRequest pageRequest = PageRequest.of( 0, 10);
		List<Loan> list = Arrays.asList(loan);
		Page<Loan> page = new PageImpl<Loan>(list, pageRequest, list.size());
		when( repository.findByBookIsbnOrCustomer(
											Mockito.anyString(),
											Mockito.anyString(), 
											Mockito.any(PageRequest.class)))
						.thenReturn(page);

		Page<Loan> result = service.find( loanFilterDto, pageRequest);
		
		assertThat( result.getTotalElements() ).isEqualTo(1);
		assertThat( result.getContent() ).isEqualTo( list );
		assertThat( result.getPageable().getPageNumber() ).isEqualTo(0);
		assertThat( result.getPageable().getPageSize() ).isEqualTo(10);
		
	}
	
}
