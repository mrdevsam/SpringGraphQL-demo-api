package dev.mrdevsam.projects.example.springgraphqldemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.persistence.*;
import lombok.*;
//import java.util.*;
//import org.springframework.graphql.data.method.annotation.*;
//import org.springframework.stereotype.Controller;
//import org.springframework.stereotype.Service;
//import jakarta.annotation.PostConstruct;
//import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
public class SpringGraphqlDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringGraphqlDemoApplication.class, args);
	}

}

//Data layer

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
class Address {

	@Id
	@GeneratedValue
	private Integer id;

	private String address;
	private String city;
	private String state;
	private String zip;
}

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
class Person {

	@Id
	@GeneratedValue
	private Integer id;

	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String email;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "address_id", referencedColumnName = "id")
	private Address address;
}
