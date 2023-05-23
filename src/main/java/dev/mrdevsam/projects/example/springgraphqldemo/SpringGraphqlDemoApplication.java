package dev.mrdevsam.projects.example.springgraphqldemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import graphql.scalars.ExtendedScalars;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

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
