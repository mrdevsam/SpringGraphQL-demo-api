package dev.mrdevsam.projects.example.springgraphqldemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.repository.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import com.github.javafaker.Faker;
import java.util.*;
import java.util.stream.*;
import org.springframework.graphql.data.method.annotation.*;


@SpringBootApplication
public class SpringGraphqlDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringGraphqlDemoApplication.class, args);
	}

}

//Data layer

@Entity
@NoArgsConstructor
@Data
class Address {

	@Id
	@GeneratedValue
	private Integer id;

	private String address;
	private String city;
	private String state;
	private String zip;

	public Address(String address, String city, String state, String zip) {
		this.address = address;
		this.city = city;
		this.state = state;
		this.zip = zip;
	}
}

@Entity
@NoArgsConstructor
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

	public Person(String firstName, String lastName, String phoneNumber, String email, Address address) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.address = address;
	}
}

//repository layer

interface PersonRepo extends PagingAndSortingRepository<Person, Integer>, CrudRepository<Person, Integer> {
	
}

//Data Loader

@Component
class SampleDataLoader implements CommandLineRunner {

	private final PersonRepo repo;
	private final Faker faker;

	public SampleDataLoader(PersonRepo repository) {
		this.repo = repository;
		this.faker = new Faker();
	}
	
	@Override
	public void run(String... args) throws Exception {

		//create 100 rows of people in database
		List<Person> people = IntStream.rangeClosed(1, 100)
			.mapToObj(i -> new Person(
				faker.name().firstName(),
				faker.name().lastName(),
				faker.phoneNumber().cellPhone(),
				faker.internet().emailAddress(),
				new Address(
					faker.address().streetAddress(),
					faker.address().city(),
					faker.address().state(),
					faker.address().zipCode()
				)
			)).toList();

		repo.saveAll(people);
	}
}
//controller layer

@RestController
@RequestMapping("/api/people")
class PersonController {

	private final PersonRepo pRepo;

	PersonController(PersonRepo pRepo) {
		this.pRepo = pRepo;
	}

	@GetMapping
	public Page<Person> findAll(@RequestParam int page, @RequestParam int size) {
		PageRequest pr = PageRequest.of(page, size);
		return pRepo.findAll(pr);
	}
}

@Controller
class PersonGraphQlController {

	private final PersonRepo repo;

	PersonGraphQlController(PersonRepo repo) {
		this.repo = repo;
	}

	//@SchemaMapping(typeName = "Query", value = "allPeople")
	@QueryMapping
	public Iterable<Person> allPeople() {
		return repo.findAll();
	}

	@QueryMapping
	public Page<Person> allPeoplePaged(@Argument int page, @Argument int size) {
		PageRequest pr = PageRequest.of(page, size);
		return repo.findAll(pr);
	}
}
