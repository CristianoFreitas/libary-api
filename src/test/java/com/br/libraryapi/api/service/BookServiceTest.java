package com.br.libraryapi.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.br.libraryapi.api.model.entity.Book;
import com.br.libraryapi.api.model.repository.BookRepository;
import com.br.libraryapi.api.service.impl.BookServiceImpl;
import com.br.libraryapi.exception.BusinessException;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test") 
public class BookServiceTest {

	BookService service;
	
	@MockBean
	BookRepository repository;
	
	@BeforeEach
	public void setUp() {
		this.service = new BookServiceImpl( repository );
	}
	
	@Test
	@DisplayName("Deve salvar um livro")
	public void saveBookTest() {
		
		//cenário
		Book book = createValidBook();
		Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
		
		Mockito.when( repository.save(book) ).thenReturn(Book.builder() // Simula retorno 
				.id(Long.valueOf(11)).isbn("123").author("Beltrano").title("Aventuras").build());
		
		
		//execucao
		Book savedBook = service.save(book);
		
		//verificacao
		assertThat(savedBook.getId()).isNotNull();
		assertThat(savedBook.getIsbn()).isEqualTo(book.getIsbn());
		assertThat(savedBook.getAuthor()).isEqualTo(book.getAuthor());
		assertThat(savedBook.getTitle()).isEqualTo(book.getTitle());
	
	}
	
	@Test
	@DisplayName("deve lançar erro de negocio  ao tentar salvar um livro com isbn duplicado")
	public void shouldNotSaveABookWithDuplicatedISBN() { 
		
		Book book = createValidBook();
		String messageError = "Isbn já cadastrado";
		Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);
		
		//verificação
		Throwable exception = Assertions.catchThrowable( () -> service.save(book));
		assertThat(exception)
			.isInstanceOf(BusinessException.class)
			.hasMessage(messageError);
		
		Mockito.verify(repository, Mockito.never()).save(book);
		
	}
	
	private Book createValidBook() {
		return Book.builder().isbn("123").author("Beltrano").title("Aventuras").build();
	}
	
}
