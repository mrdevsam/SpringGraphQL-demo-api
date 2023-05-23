package dev.mrdevsam.projects.example.springgraphqldemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import graphql.scalars.ExtendedScalars;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.data.repository.ListCrudRepository;

@SpringBootApplication
public class SpringGraphqlDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringGraphqlDemoApplication.class, args);
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
	private LocalDate creationDate;

	public Product(String title, Boolean isOnSale, Float weight, BigDecimal price, LocalDate creationDate) {
		this.title = title;
		this.isOnSale = isOnSale;
		this.weight = weight;
		this.price = price;
		this.creationDate = creationDate;
	}
	
}

//repository
interface ProductRepo extends ListCrudRepository<Product, Integer> {}
