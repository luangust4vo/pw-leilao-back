package com.github.luangust4vo.pw_leilao_backend.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.github.luangust4vo.pw_leilao_backend.dto.ChangePasswordRequestDTO;
import com.github.luangust4vo.pw_leilao_backend.dto.PersonResponseDTO;
import com.github.luangust4vo.pw_leilao_backend.dto.UpdatePersonRequestDTO;
import com.github.luangust4vo.pw_leilao_backend.exception.BusinessException;
import com.github.luangust4vo.pw_leilao_backend.exception.NotFoundException;
import com.github.luangust4vo.pw_leilao_backend.models.Person;
import com.github.luangust4vo.pw_leilao_backend.repositories.PersonRepository;

@Service
public class PersonService implements UserDetailsService {
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

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

    public Person update(Long id, UpdatePersonRequestDTO updatePersonRequestDTO) {
        Person existingPerson = this.findById(id);

        existingPerson.setName(updatePersonRequestDTO.getName());

        return personRepository.save(existingPerson);
    }

    public void delete(Long id) {
        Person existingPerson = this.findById(id);

        personRepository.delete(existingPerson);
    }

    public void changeStatus(Long id, boolean active) {
        Person existingPerson = this.findById(id);
        existingPerson.setActive(active);
        personRepository.save(existingPerson);
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

    public void changePassword(Person person, ChangePasswordRequestDTO dto) {
        if (!passwordEncoder.matches(dto.getCurrentPassword(), person.getPassword())) {
            throw new BusinessException("A senha atual está incorreta."); 
        }

        if (passwordEncoder.matches(dto.getNewPassword(), person.getPassword())) {
            throw new BusinessException("A nova senha não pode ser igual à antiga.");
        }

        person.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        personRepository.save(person);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return personRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Pessoa não encontrada"));
    }
}
