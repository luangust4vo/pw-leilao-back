package com.github.luangust4vo.pw_leilao_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import com.github.luangust4vo.pw_leilao_backend.exception.NotFoundException;
import com.github.luangust4vo.pw_leilao_backend.models.Person;
import com.github.luangust4vo.pw_leilao_backend.repositories.PersonRepository;
import com.github.luangust4vo.pw_leilao_backend.utils.Const;

@Service
public class PersonService {
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private EmailService emailService;

    private void sendSuccessEmail(Person person) {
        Context context = new Context(LocaleContextHolder.getLocale());
        context.setVariable("name", person.getName());
        emailService.emailTemplate(person.getEmail(), "Cadastrado com sucesso", Const.templateSuccessRegister, context);
    }

    public Person findById(Long id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("person.not-found", new Object[] { id },
                                LocaleContextHolder.getLocale())));
    }

    public Page<Person> findAll(Pageable pageable) {
        return personRepository.findAll(pageable);
    }

    public Person create(Person person) {
        Person newPerson = personRepository.save(person);
        sendSuccessEmail(newPerson);

        return newPerson;
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
