package com.br.libraryapi.api.service;

import static org.assertj.core.api.Assertions.assertThat;

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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.br.libraryapi.api.service.impl.BookServiceImpl;
import com.br.libraryapi.exception.BusinessException;
import com.br.libraryapi.model.entity.Book;
import com.br.libraryapi.model.repository.BookRepository;
import com.br.libraryapi.service.BookService;

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

	@Test
	@DisplayName("Deve obter um livro por id")
	public void getById() {
		Long id = 1l;
		
		// Cenario
		Book book = createValidBook();
		book.setId(id);
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));
		
		// Execucao
		Optional<Book> foundBook = service.getById(id);
		
		// Verificacao
		assertThat( foundBook.isPresent() ).isTrue();
		assertThat( foundBook.get().getId() ).isEqualTo(book.getId());
		assertThat( foundBook.get().getAuthor() ).isEqualTo(book.getAuthor());
		assertThat( foundBook.get().getIsbn() ).isEqualTo(book.getIsbn());
		assertThat( foundBook.get().getTitle() ).isEqualTo(book.getTitle());
		
	}

	@Test
	@DisplayName("Deve retornar vazio ao pesquisar book por id nao existente")
	public void bookNotFoundById() {
		Long id = 1l;
		
		// Cenario
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
		
		// Execucao
		Optional<Book> foundBook = service.getById(id);
		
		// Verificacao
		assertThat( foundBook.isPresent() ).isFalse();
	}
	
	@Test
	@DisplayName("deve deletar um livros")
	public void shouldDeleteABook() { 
		Long id = 1l;
		
		Book book = createValidBook();
		book.setId(id);
		
		service.delete(book);
//		 or  >>  org.junit.jupiter.api.Assertions.assertDoesNotThrow( () -> service.delete(book));
		
		Mockito.verify(repository, Mockito.only()).delete(book);
//		 or  >   Mockito.verify(repository, Mockito.times( 1 )).delete(book);
		
	}
	
	@Test
	@DisplayName("deve lançar erro de negocio  ao tentar deletar um livro inexistente")
	public void shouldDontDeleteABook() { 
		
		Book book = createValidBook();
		
		//verificação
		Throwable exception = Assertions.catchThrowable( () -> service.delete(book));
		assertThat(exception)
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("O Id do Livro não pode ser nulo");
		
		Mockito.verify(repository, Mockito.never()).delete(book);
		
	}

	@Test
	@DisplayName("deve lançar erro ao tentar atualizar um livro")
	public void shouldInvalidUpdateABook() { 
		
		Book book = new Book();
		
		//verificação
		Throwable exception = Assertions.catchThrowable( () -> service.update(book));
		assertThat(exception)
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("O Id do Livro não pode ser nulo");
		
		Mockito.verify(repository, Mockito.never()).save(book);
		
	}
	
	@Test
	@DisplayName("Deve atualizar um livro")
	public void updateBook() {
		
		Long id = 1l;
		
		Book updatingBook = Book.builder().id(id).build();
		
		Book updatedBook = createValidBook(); 
		updatedBook.setId(id);
		Mockito.when(repository.save(updatingBook)).thenReturn(updatedBook);
		
		Book book = service.update(updatingBook);
		
		assertThat( book.getId() ).isEqualTo( updatedBook.getId() );
		assertThat( book.getIsbn() ).isEqualTo( updatedBook.getIsbn() );
		assertThat( book.getAuthor() ).isEqualTo( updatedBook.getAuthor() );
		assertThat( book.getTitle() ).isEqualTo( updatedBook.getTitle() );
		
	}

	@Test
	@DisplayName("Deve filtrar livros pelas propriedades")
	public void findBook() { 
		
		Book book = createValidBook();
		List<Book> list = Arrays.asList(book);
		PageRequest pageRequest = PageRequest.of( 0, 10);
		Page<Book> page = new PageImpl<Book>(list, pageRequest, 1);
		Mockito.when( repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
				.thenReturn(page);
		
		Page<Book> result = service.find( book, pageRequest);
		
		assertThat( result.getTotalElements() ).isEqualTo(1);
		assertThat( result.getContent() ).isEqualTo( list );
		assertThat( result.getPageable().getPageNumber() ).isEqualTo(0);
		assertThat( result.getPageable().getPageSize() ).isEqualTo(10);
		
	}
	
	private Book createValidBook() {
		return Book.builder().isbn("123").author("Beltrano").title("Aventuras").build();
	}
	
}
