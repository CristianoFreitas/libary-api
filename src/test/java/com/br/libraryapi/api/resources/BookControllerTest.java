package com.br.libraryapi.api.resources;


import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.br.libraryapi.api.dto.BookDTO;
import com.br.libraryapi.api.model.entity.Book;
import com.br.libraryapi.api.service.BookService;
import com.br.libraryapi.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class) // Junit 5
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

	static String BOOK_API = "/api/books";
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	BookService service;
	
	@Test
	@DisplayName("Deve criar um livro com sucesso.")
	public void createBookTest() throws Exception {
		
		BookDTO dto = createNewBook();
		Book entity = Book.builder().id(Long.valueOf(10)).author("Autor").title("Meu Livro").isbn("001").build();
		BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(entity);
		String json = new ObjectMapper().writeValueAsString(dto);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.post(BOOK_API)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.content(json);
			
		mvc.perform(request)
			.andExpect( MockMvcResultMatchers.status().isCreated() )
			.andExpect( MockMvcResultMatchers.jsonPath("id").value(dto.getId()) )
			.andExpect( MockMvcResultMatchers.jsonPath("title").value(dto.getTitle()) )
			.andExpect( MockMvcResultMatchers.jsonPath("author").value(dto.getAuthor()) )
			.andExpect( MockMvcResultMatchers.jsonPath("isbn").value(dto.getIsbn()) );
		
	}

	
	@Test
	@DisplayName("Deve lançar erro de validação quando houver dados insuficientes para criação do livro.")
	public void createInvalidBookTest() throws Exception {
		
		BookDTO dto = new BookDTO();
		String json = new ObjectMapper().writeValueAsString(dto);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc.perform(request)
			.andExpect( status().isBadRequest() )
			.andExpect( jsonPath("errors", hasSize(3)));
	}
	
	@Test
	@DisplayName("deve lançar erro ao tentar cadastra0r um livro com isbn já utlizado por outro.")
	public void createBookWithDuplicatedIsnb() throws Exception {
		
		BookDTO dto = createNewBook();
		String json = new ObjectMapper().writeValueAsString(dto);
		String messageError = "Isbn já cadastrado";
		BDDMockito.given(service.save(Mockito.any(Book.class))).willThrow(new BusinessException(messageError));
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);

		mvc.perform( request )
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("errors", hasSize(1)))
			.andExpect(jsonPath("errors[0]").value(messageError));
		
	}
	
	private BookDTO createNewBook() {
		return BookDTO.builder().id(Long.valueOf(10)).author("Autor").title("Meu Livro").isbn("001").build();
	} 
}
