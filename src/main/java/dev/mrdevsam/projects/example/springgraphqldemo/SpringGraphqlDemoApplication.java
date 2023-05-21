package dev.mrdevsam.projects.example.springgraphqldemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.context.annotation.*;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import static org.springframework.security.config.Customizer.withDefaults;

@SpringBootApplication
public class SpringGraphqlDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringGraphqlDemoApplication.class, args);
	}

}

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled=true)
class SecurityConfig {

	@Bean
	public InMemoryUserDetailsManager userDetailsManager() {
		UserDetails user = User.withDefaultPasswordEncoder()
								.username("user")
								.password("passwordu")
								.roles("USER")
								.build();

		UserDetails admin = User.withDefaultPasswordEncoder()
								.username("admin")
								.password("passwrda")
								.roles("USER", "ADMIN")
								.build();

		return new InMemoryUserDetailsManager(user,admin);
	}
	
	@Bean
	public SecurityFilterChain configure(HttpSecurity http) throws Exception {
		return http
			.csrf(csrf -> csrf.disable()) 
			.authorizeRequests( auth -> {
				auth.anyRequest().authenticated();
			}) // allow all requests for a authentiocated user
			.sessionManagement(
				session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			) // disable session management
			.httpBasic(withDefaults()).build();
	}
}
//data models
record Coffee(Integer id, String name, Size size){
	
}

enum Size {
	SHORT, MEDIUM, TALL
}

//Service layer
@Service
class CoffeeService{

	private List<Coffee> coffees = new ArrayList<>();
	AtomicInteger id = new AtomicInteger(0);

	public List<Coffee> findAll() {
		return coffees;
	}

	public Optional<Coffee> findOne(Integer id) {
		return coffees.stream().filter(coffee -> coffee.id() ==id).findFirst();
	}

	public Coffee create(String name, Size size) {
		Coffee coffee = new Coffee(id.incrementAndGet(), name, size);
		coffees.add(coffee);
		return coffee;
	}
	
	public Coffee update(Integer id, String name, Size size) {
	
		Coffee updatedCoffee = new Coffee(id, name, size);
		Optional<Coffee> optional = coffees.stream().filter(c -> c.id() ==id).findFirst();

		if(optional.isPresent()) {
			Coffee coffee = optional.get();
			int index = coffees.indexOf(coffee);
			coffees.set(index, updatedCoffee);
		} else {
			throw new IllegalArgumentException("Invalid coffee");
		}

		return updatedCoffee;
	}
	
	public Coffee delete(Integer id) {
		Coffee coffee = coffees.stream().filter(c -> c.id() == id)
							.findFirst().orElseThrow( () -> new IllegalArgumentException() );
		coffees.remove(coffee);
		return coffee;
	}

	@PostConstruct
	private void init() {
		coffees.add(new Coffee(id.incrementAndGet(), "AAaa", Size.TALL));
		coffees.add(new Coffee(id.incrementAndGet(), "BBbb", Size.SHORT));
		coffees.add(new Coffee(id.incrementAndGet(), "CCcc", Size.MEDIUM));
		coffees.add(new Coffee(id.incrementAndGet(), "VVvv", Size.SHORT));
	}
}


//graphql controller
@Controller
class CoffeeController{

	private final CoffeeService coffeeService;

	public CoffeeController(CoffeeService coffeeService) {
		this.coffeeService = coffeeService;
	}

	@Secured("ROLE_USER") //we can use @PreAuthorize in place of @Secured
	@QueryMapping
	public List<Coffee> findAll() {
		return coffeeService.findAll();
	}

	@Secured("ROLE_USER")
	@QueryMapping
	public Optional<Coffee> findOne(@Argument Integer id) {
		return coffeeService.findOne(id);
	}

	@PreAuthorize("hasRole('ADMIN')") // we can use @Secured in place of @PreAuthorize
	@MutationMapping
	public Coffee create(@Argument String name, @Argument Size size) {
		return coffeeService.create(name, size);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@MutationMapping
	public Coffee update(@Argument Integer id, @Argument String name, @Argument Size size) {
		return coffeeService.update(id, name, size);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@MutationMapping
	public Coffee delete(@Argument Integer id) {
		return coffeeService.delete(id);
	}
}
