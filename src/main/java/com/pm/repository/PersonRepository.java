package com.pm.repository;

import com.pm.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Collection<Person> findByLastName(final String lastName);
}
