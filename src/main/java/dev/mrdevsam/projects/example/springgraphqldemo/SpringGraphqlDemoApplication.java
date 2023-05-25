package dev.mrdevsam.projects.example.springgraphqldemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;
//import org.springframework.graphql.data.method.annotation.*;
//import org.springframework.stereotype.Controller;
//import org.springframework.stereotype.Service;
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
