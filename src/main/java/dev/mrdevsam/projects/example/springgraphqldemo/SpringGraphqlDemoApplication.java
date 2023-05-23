package dev.mrdevsam.projects.example.springgraphqldemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import graphql.scalars.ExtendedScalars;
import org.springframework.stereotype.Controller;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.data.repository.ListCrudRepository;

@SpringBootApplication
public class SpringGraphqlDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringGraphqlDemoApplication.class, args);
	}

	@Bean
	CommandLineRunner bootstrap(ProductRepo repository) {
		return args -> {
			List<Product> products = List.of(
				new Product("AAA", true, 1.54F, new BigDecimal(4.34), LocalDateTime.now()),
				new Product("BBB", true, 6.23F, new BigDecimal(8.56), LocalDateTime.now()),
				new Product("CCC", true, 3.96F, new BigDecimal(3.75), LocalDateTime.now())
			);

			repository.saveAll(products);
			repository.findAll().forEach(System.out::println);
		};
	}

}


//configurations
@Configuration
class GraphqlConfig {

	@Bean
	public RuntimeWiringConfigurer configurer() {
		return wiringBuilder -> wiringBuilder
								.scalar(ExtendedScalars.GraphQLBigDecimal);
	}
}

// data models
@Entity
@NoArgsConstructor
@Data
class Product {

	@Id
	@GeneratedValue
	private Integer id;
	private String title;
	private Boolean isOnSale;
	private Float weight;
	private BigDecimal price;
	private LocalDateTime creationDate;

	public Product(String title, Boolean isOnSale, Float weight, BigDecimal price, LocalDateTime creationDate) {
		this.title = title;
		this.isOnSale = isOnSale;
		this.weight = weight;
		this.price = price;
		this.creationDate = creationDate;
	}
	
}

//repository
interface ProductRepo extends ListCrudRepository<Product, Integer> {}

//controller
@Controller
class ProductController {

	private final ProductRepo repo;

	ProductController(ProductRepo repo) {
		this.repo = repo;
	}

	@QueryMapping
	public List<Product> allProducts() {
		return repo.findAll();
	}
}
