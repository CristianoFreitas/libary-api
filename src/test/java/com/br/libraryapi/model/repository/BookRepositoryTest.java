package com.br.libraryapi.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.br.libraryapi.model.entity.Book;
import com.br.libraryapi.model.repository.BookRepository;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

	@Autowired
	TestEntityManager entityManger;
	
	@Autowired
	BookRepository repository;
	
	@Test
	@DisplayName("Deve retornar verdadeiro quando existir um livro na base com Isbn informado.")
	public void returnTrueWhenIsbnExistis() {
		String isbn = "123";
		Book book = Book.builder().title("As aventuras").author("Beltrano").isbn(isbn).build();
		entityManger.persist(book);
		
		boolean exists = repository.existsByIsbn(isbn);	
	
		assertThat(exists).isTrue();
	}
	
	@Test
	@DisplayName("Deve retornar falso quando não existir um livro na base com Isbn informado.")
	public void returnFalseWhenIsbnExistis() {
		String isbn = "123";
		
		boolean exists = repository.existsByIsbn(isbn);	
	
		assertThat(exists).isFalse();
	}
	
	@Test
	@DisplayName("Deve salvar um livro")
	public void saveBookTest() { 
		
		Book book = createNewBook("123");
		
		Book savedBook = repository.save(book);
		
		assertThat( savedBook.getId() ).isNotNull();
			
	}
	
	@Test
	@DisplayName("Deve deletar um livro")
	public void deleteBookTest() { 
		
		Book book = createNewBook("123");
		entityManger.persist(book);
		
		Book foundBook = entityManger.find(Book.class, book.getId());
		
		repository.delete(foundBook);
		
		Book deletedBook = entityManger.find(Book.class, book.getId());
		assertThat( deletedBook ).isNull(); 
			
	}
	
	public static Book createNewBook(String isbn) {
		return Book.builder().isbn(isbn).author("Beltrano").title("Aventuras").build();
	}
}
