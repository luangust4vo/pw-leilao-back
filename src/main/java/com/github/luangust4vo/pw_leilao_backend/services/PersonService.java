package com.github.luangust4vo.pw_leilao_backend.services;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.github.luangust4vo.pw_leilao_backend.exception.NotFoundException;
import com.github.luangust4vo.pw_leilao_backend.models.Person;
import com.github.luangust4vo.pw_leilao_backend.repositories.PersonRepository;

@Service
public class PersonService {
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private MessageSource messageSource;

    public Person findById(Long id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("person.not-found", new Object[] { id },
                                LocaleContextHolder.getLocale())));
    }

    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public Person create(Person person) {
        return personRepository.save(person);
    }

    public Person update(Person person) {
        Person existingPerson = this.findById(person.getId());

        existingPerson.setName(person.getName());
        existingPerson.setEmail(person.getEmail());

        return personRepository.save(existingPerson);
    }

    public void delete(Long id) {
        Person existingPerson = this.findById(id);

        personRepository.delete(existingPerson);
    }
}
