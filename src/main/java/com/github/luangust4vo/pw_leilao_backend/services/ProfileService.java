package com.github.luangust4vo.pw_leilao_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.github.luangust4vo.pw_leilao_backend.exception.NotFoundException;
import com.github.luangust4vo.pw_leilao_backend.models.Profile;
import com.github.luangust4vo.pw_leilao_backend.repositories.ProfileRepository;

@Service
public class ProfileService {
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private MessageSource messageSource;

    public Profile findById(Long id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("profile.not-found", new Object[] { id },
                                LocaleContextHolder.getLocale())));
    }

    public Page<Profile> findAll(Pageable pageable) {
        return profileRepository.findAll(pageable);
    }

    public Profile create(Profile profile) {
        return profileRepository.save(profile);
    }

    public Profile update(Profile profile) {
        Profile existingProfile = this.findById(profile.getId());

        return profileRepository.save(existingProfile);
    }

    public void delete(Long id) {
        Profile existingProfile = this.findById(id);

        profileRepository.delete(existingProfile);
    }
}
