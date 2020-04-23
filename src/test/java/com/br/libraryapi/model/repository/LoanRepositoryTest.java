package com.br.libraryapi.model.repository;

import static com.br.libraryapi.model.repository.BookRepositoryTest.createNewBook;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.br.libraryapi.model.entity.Book;
import com.br.libraryapi.model.entity.Loan;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

	@Autowired
	LoanRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	@DisplayName("Deve verificar se existe empréstimo não devolvido para o livro")
	public void existsBookAndNotReturnedTest() throws Exception {
		
		Loan loan = createAndPersistLoan();
		
		boolean exists = repository.existsByBookAndNotReturned(loan.getBook());
		
		assertThat(exists).isTrue();
	}

	@Test
	@DisplayName("Deve buscar um emprestimo por isbn do livro ou customer")
	public void findByBookIsbnOrCustomer() throws Exception {
		Loan loan = createAndPersistLoan();
		
		Page<Loan> result = repository.findByBookIsbnOrCustomer(loan.getBook().getIsbn(), loan.getCustomer(), PageRequest.of(0, 10));
		
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getPageable().getPageSize()).isEqualTo(10);
		assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
		assertThat(result.getTotalElements()).isEqualTo(1);
		
	}
		
	@Test
	@DisplayName("Deve obter emprestimos cuja data de empréstimos for menor ou igua a tres dias atrás e não retornado")
	public void findByLoanDateLessThanAndNotReturned() throws Exception {
		Loan loan = createAndPersistLoan(LocalDate.now().minusDays(5));
		
		List<Loan> result = repository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));
		
		assertThat(result).hasSize(1).contains(loan);
	}
	
	@Test
	@DisplayName("Deve retornar vazio quando não houver emprestimos atrasados")
	public void notfindByLoanDateLessThanAndNotReturned() throws Exception {
		createAndPersistLoan(LocalDate.now());
		
		List<Loan> result = repository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));
		
		assertThat(result).isEmpty();
	}

	public Loan createAndPersistLoan() throws Exception {
		return createAndPersistLoan(LocalDate.now());
	}
	
	public Loan createAndPersistLoan(LocalDate loanDate) throws Exception {
		Book book = createNewBook("123");
		entityManager.persist(book);
		
		Loan loan = Loan.builder().book(book).customer("Beltrano").loanDate(loanDate).build();
		entityManager.persist(loan);
		
		return loan;
	}

}
