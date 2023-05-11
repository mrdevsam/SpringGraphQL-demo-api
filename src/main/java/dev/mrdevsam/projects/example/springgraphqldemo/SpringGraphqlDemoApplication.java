package dev.mrdevsam.projects.example.springgraphqldemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
public class SpringGraphqlDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringGraphqlDemoApplication.class, args);
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

	@QueryMapping
	public List<Coffee> findAll() {
		return coffeeService.findAll();
	}

	@QueryMapping
	public Optional<Coffee> findOne(@Argument Integer id) {
		return coffeeService.findOne(id);
	}

	@MutationMapping
	public Coffee create(@Argument String name, @Argument Size size) {
		return coffeeService.create(name, size);
	}

	@MutationMapping
	public Coffee update(@Argument Integer id, @Argument String name, @Argument Size size) {
		return coffeeService.update(id, name, size);
	}

	@MutationMapping
	public Coffee delete(@Argument Integer id) {
		return coffeeService.delete(id);
	}
}
