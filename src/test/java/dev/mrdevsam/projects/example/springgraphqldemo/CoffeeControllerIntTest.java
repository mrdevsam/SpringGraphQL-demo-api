package dev.mrdevsam.projects.example.springgraphqldemo;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@GraphQlTest(CoffeeController.class)
@Import(CoffeeService.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CoffeeControllerIntTest {

	@Autowired
	GraphQlTester graphQlTester;

	@Autowired
	CoffeeService coffeeService;

	@Test
	@Order(1)
	void testFindAllShouldReturnAllCoffees() {

		String document = """
		query {
			findAll{
				id
				name
				size
			}
		}
		""";

		graphQlTester.document(document)
			.execute()
			.path("findAll")
			.entityList(Coffee.class)
			.hasSize(4);
	}

	@Test
	@Order(2)
	void testFindOneShouldReturnACoffeeOfSpecifiedId() {
	
		String document = """
		query findOneCoffee($id: ID) {
			findOne(id: $id){
				id
				name
				size
			}
		}
		""";

		graphQlTester.document(document)
			.variable("id", 1)
			.execute()
			.path("findOne")
			.entity(Coffee.class)
			.satisfies(coffee -> {
				assertEquals("AAaa", coffee.name());
				assertEquals(Size.TALL, coffee.size());
			});
	}

	@Test
	@Order(3)
	void testFindOneAndInvalidIdShouldReturnNull() {
	
		String document = """
		query findOneCoffee($id: ID) {
			findOne(id: $id){
				id
				name
				size
			}
		}
		""";

		graphQlTester.document(document)
			.variable("id", 66)
			.execute()
			.path("findOne")
			.valueIsNull();
	}

	@Test
	@Order(4)
	void testCreateNewCoffee() {

		int currentCoffeeCount = coffeeService.findAll().size();
		
		String document = """
		mutation createCoffee($name: String,$size: Size) {
			create(name: $name, size: $size) {
				id
				name
				size
			}
		}
		""";

		graphQlTester.document(document)
			.variable("name", "EEee")
			.variable("size", Size.MEDIUM)
			.execute()
			.path("create")
			.entity(Coffee.class)
			.satisfies(coffee -> {
				assertNotNull(coffee.id());
				assertEquals("EEee", coffee.name());
				assertEquals(Size.MEDIUM, coffee.size());
			});

		assertEquals(currentCoffeeCount + 1, coffeeService.findAll().size());
	}

	@Test
	@Order(5)
	void testUpdateACoffeeDetails() {

		Coffee currentCoffee = coffeeService.findOne(5).get();
		
		String document = """
		mutation updateCoffee($id: ID,$name: String,$size: Size) {
			update(id: $id,name: $name, size: $size) {
				id
				name
				size
			}
		}
		""";

		graphQlTester.document(document)
			.variable("id", 5)
			.variable("name", "EEeeUpdated")
			.variable("size", Size.TALL)
			.execute()
			.path("update")
			.entity(Coffee.class);

		Coffee updateCoffee = coffeeService.findOne(5).get();
		assertNotEquals(currentCoffee, updateCoffee);
		assertEquals("EEeeUpdated", updateCoffee.name());
		assertEquals(Size.TALL, updateCoffee.size());
	}

	@Test
	@Order(6)
	void testDeleteACoffee() {

		int cCount = coffeeService.findAll().size();

		String document = """
		mutation deleteCoffee($id: ID) {
			delete(id: $id){
				id
				name
				size
			}
		}
		""";

		graphQlTester.document(document)
			.variable("id", 5)
			.executeAndVerify();

		assertEquals(cCount - 1, coffeeService.findAll().size());
	}
}
