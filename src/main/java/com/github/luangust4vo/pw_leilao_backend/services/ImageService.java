package com.github.luangust4vo.pw_leilao_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.luangust4vo.pw_leilao_backend.dto.AvatarRequestDTO;
import com.github.luangust4vo.pw_leilao_backend.models.Person;

@Service
public class ImageService {
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private PersonService personService;

    public String uploadAvatar(AvatarRequestDTO request, String folder) {
        try {
            String imageUrl = cloudinaryService.uploadFile(request.getFile(), folder);
            Person user = personService.findById(request.getUserId());

            user.setProfileImage(imageUrl);
            personService.update(user);

            return imageUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }
}