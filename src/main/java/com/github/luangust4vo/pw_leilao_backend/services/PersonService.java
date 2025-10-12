package com.github.luangust4vo.pw_leilao_backend.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.github.luangust4vo.pw_leilao_backend.dto.PersonResponseDTO;
import com.github.luangust4vo.pw_leilao_backend.exception.NotFoundException;
import com.github.luangust4vo.pw_leilao_backend.models.Person;
import com.github.luangust4vo.pw_leilao_backend.repositories.PersonRepository;

@Service
public class PersonService implements UserDetailsService {
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

    public Page<Person> findAll(Pageable pageable) {
        return personRepository.findAll(pageable);
    }

    public Person create(Person person) {
        Person newPerson = personRepository.save(person);
        return newPerson;
    }

    public Person update(Person person) {
        Person existingPerson = this.findById(person.getId());

        existingPerson.setName(person.getName());
        existingPerson.setEmail(person.getEmail());
        existingPerson.setActive(person.isActive());
        existingPerson.setProfileImage(person.getProfileImage());
        existingPerson.setPersonProfiles(person.getPersonProfiles());

        return personRepository.save(existingPerson);
    }

    public void delete(Long id) {
        Person existingPerson = this.findById(id);

        personRepository.delete(existingPerson);
    }

    public PersonResponseDTO findPersonById(Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("person.not-found", new Object[] { id },
                                LocaleContextHolder.getLocale())));
        return convertToPersonResponseDTO(person);
    }

    public Page<PersonResponseDTO> findAllPeople(Pageable pageable) {
        Page<Person> personPage = personRepository.findAll(pageable);
        List<PersonResponseDTO> personDTOs = personPage.getContent().stream()
                .map(this::convertToPersonResponseDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(personDTOs, pageable, personPage.getTotalElements());
    }

    private PersonResponseDTO convertToPersonResponseDTO(Person person) {
        PersonResponseDTO dto = new PersonResponseDTO();
        dto.setId(person.getId());
        dto.setName(person.getName());
        dto.setEmail(person.getEmail());
        dto.setActive(person.isActive());
        dto.setProfileImage(person.getProfileImage());
        dto.setCreatedAt(person.getCreatedAt());
        
        List<PersonResponseDTO.PersonProfileInfo> profileInfos = person.getPersonProfiles().stream()
                .map(pp -> {
                    PersonResponseDTO.PersonProfileInfo.ProfileInfo profileInfo = 
                            new PersonResponseDTO.PersonProfileInfo.ProfileInfo(
                                    pp.getProfile().getId(),
                                    pp.getProfile().getType()
                            );
                    return new PersonResponseDTO.PersonProfileInfo(profileInfo);
                })
                .collect(Collectors.toList());
        
        dto.setPersonProfiles(profileInfos);
        return dto;
    }

    public Person findByEmail(String email) {
        return personRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("person.not-found", new Object[] { email },
                                LocaleContextHolder.getLocale())));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return personRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Pessoa não encontrada"));
    }
}
