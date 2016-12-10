package com.pm.controller;

import com.pm.entity.Person;
import com.pm.repository.PersonRepository;
import com.pm.util.exception.PersonNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/person")
public class PersonRestController {
    private final PersonRepository personRepository;

    @Autowired
    private PersonRestController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Collection<Person> getAll(@RequestParam(value = "search", required = false) String search) {
        if (search == null) {
            return personRepository.findAll();
        } else {
            return personRepository.findByLastName(search);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{personId}")
    public Person getById(@PathVariable Long personId) {
        return Optional.ofNullable(
                personRepository.findOne(personId)
        )
        .orElseThrow(
                () -> new PersonNotFoundException(String.valueOf(personId))
        );
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody Person person) {
        final Person savedPerson = personRepository.save(person);

        return ResponseEntity.created(
                getPersonLocation(savedPerson.getPersonId())
        ).build();
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{personId}")
    public ResponseEntity<?> update(@PathVariable Long personId, @RequestBody Person person) {
        final Person updatedPerson = Optional.ofNullable(
                personRepository.findOne(personId)
        )
        .orElseThrow(
                () -> new PersonNotFoundException(String.valueOf(personId))
        );

        updatedPerson.setFirstName(person.getFirstName());
        updatedPerson.setLastName(person.getLastName());
        personRepository.save(updatedPerson);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(updatedPerson);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{personId}")
    public ResponseEntity<?> delete(@PathVariable Long personId) {
        personRepository.delete(personId);

        return ResponseEntity.accepted().build();
    }

    private URI getPersonLocation(final Long personId) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{personId}")
                .buildAndExpand(
                        personId
                )
                .toUri();
    }


}
