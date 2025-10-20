package com.github.luangust4vo.pw_leilao_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.luangust4vo.pw_leilao_backend.dto.AvatarRequestDTO;
import com.github.luangust4vo.pw_leilao_backend.models.Person;
import com.github.luangust4vo.pw_leilao_backend.repositories.PersonRepository;

@Service
public class ImageService {
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private PersonRepository personRepository;

    public String uploadAvatar(AvatarRequestDTO request, String folder) {
        try {
            String imageUrl = cloudinaryService.uploadFile(request.getFile(), folder);
            Person user = personRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            user.setProfileImage(imageUrl);
            personRepository.save(user);

            return imageUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}