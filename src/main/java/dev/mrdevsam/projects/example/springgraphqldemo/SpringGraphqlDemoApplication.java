package dev.mrdevsam.projects.example.springgraphqldemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.data.jpa.repository.JpaRepository;
import lombok.*;
import jakarta.persistence.*;


@SpringBootApplication
public class SpringGraphqlDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringGraphqlDemoApplication.class, args);
	}

}

//data models
@NoArgsConstructor
@Data
@Entity
class Book {

	@Id
	@GeneratedValue
	private Integer id;
	
	private String title; 
	private Integer pages;
	private String author;

	@OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Review> reviews;

	public Book(String title, Integer pages, String author) {
		this.title = title;
		this.pages = pages;
		this.author = author;
	}
}


@NoArgsConstructor
@Data
@Entity
class Review {

	@Id
	@GeneratedValue
	private Integer id;
	private String title,comment;
	
	@ManyToOne
	private Book book;

	public Review(String title, String comment) {
		this.title = title;
		this.comment = comment;
	}
}

record BookInput(String title, Integer pages, String author) {}


//repository

interface BookRepository extends JpaRepository<Book, Integer> {}

//controller
@Controller
class BookgraphqlController{

	private final BookRepository repo;

	public BookgraphqlController(BookRepository repo) {
		this.repo = repo;
	}

	@QueryMapping
	public List<Book> findAllBooks() {
		return repo.findAll();
	}

	@QueryMapping
	public Book findBook(@Argument Integer id) {
		return repo.findById(id).orElse(null);
	}

	@MutationMapping
	public Book createBook(@Argument String title,@Argument Integer pages,@Argument String author) {
		return repo.save(new Book(title, pages, author));
	}

	@MutationMapping
	public Book addBook(@Argument BookInput book) {
		return repo.save(new Book(book.title(), book.pages(), book.author()));
	}

	@MutationMapping
	public Book updateBook(@Argument Integer id, @Argument BookInput book) {
		Book bkToUp = repo.findById(id).orElse(null);

		if(bkToUp == null) {
			throw new RuntimeException("Invalid book");
		}

		bkToUp.setTitle(book.title());
		bkToUp.setPages(book.pages());
		bkToUp.setAuthor(book.author());

		repo.save(bkToUp);
		return bkToUp;
	}

	@MutationMapping
	public void deleteBook(@Argument Integer id) {
		repo.deleteById(id);
	}
}
