package com.pm;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.pm.entity.Person;
import com.pm.repository.PersonRepository;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = PeopleManagementService.class)
/**
 * Integration tests for PersonRestController, using RestAssured for easy readability.
 */
public class PersonRestControllerTest {

    @Autowired
    PersonRepository personRepository;

    Person dave;
    Person arnold;
    Person kristine;
    Person kryten;
    List<Person> defaultPeople;

    @Value("${local.server.port}")
    private int port;

    @Before
    public void setup() throws Exception {

        dave = new Person("Dave", "Lister");
        arnold = new Person("Arn]old J.", "Rimmer");
        kristine = new Person("Kristine", "Kochanski");
        kryten = new Person("Kryten", "Series 4000");

        defaultPeople = Arrays.asList(dave, arnold, kristine);

        personRepository.deleteAll();
        personRepository.save(defaultPeople);

        RestAssured.port = port;
    }

    @Test
    public void shouldGetAllPeopleStored() {
        when()
                .get("/person")
        .then()
                .statusCode(HttpStatus.SC_OK)
                .body("lastName", Matchers.hasItems(
                        defaultPeople
                                .stream()
                                .map(Person::getLastName)
                                .toArray()
                ));
    }

    @Test
    public void shouldSavePersonAndReturnHisURI() {
        given()
                .body(kryten)
                .contentType(ContentType.JSON)
        .when()
                .post("/person")
        .then()
                .statusCode(HttpStatus.SC_CREATED)
                .body("firstName", Matchers.is(kryten.getFirstName()));

    }

    @Test
    public void shouldDeletePersonAndCheckPersonIsNotThereAnymore() {
        when()
                .delete("/person/" + dave.getPersonId())
        .then()
                .statusCode(HttpStatus.SC_ACCEPTED);

        when()
                .get("/person/" + dave.getPersonId())
        .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void shouldFindPersonByHisLastName() {
        when()
                .get("/person?search=Rimmer")
        .then()
                .statusCode(HttpStatus.SC_OK)
                .body("lastName", Matchers.hasItem(arnold.getLastName()));
    }

    @Test
    public void shouldUpdatePersonsFirstName() {
        final String arnoldsFixedName = "Arnold";

        given()
                .body(new Person(arnoldsFixedName, arnold.getLastName()))
                .contentType(ContentType.JSON)
        .when()
                .put("/person/" + arnold.getPersonId())
        .then()
                .statusCode(HttpStatus.SC_OK)
                .body("firstName", Matchers.is(arnoldsFixedName));

        when()
                .get("/person/" + arnold.getPersonId())
        .then()
                .body("firstName", Matchers.is(arnoldsFixedName));

    }

    @Test
    public void shouldNotFindAnythingAndReturn404() {
        when()
                .get("/person/-1")
        .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }
}
