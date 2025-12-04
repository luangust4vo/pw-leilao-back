package com.github.luangust4vo.pw_leilao_backend.controllers;

import com.github.luangust4vo.pw_leilao_backend.dto.ApiResponseDTO;
import com.github.luangust4vo.pw_leilao_backend.dto.AvatarRequestDTO;
import com.github.luangust4vo.pw_leilao_backend.services.ImageService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/storage")
public class UploadController {
    @Autowired
    private ImageService imageService;

    @PostMapping("upload/avatar/{id}")
    public ResponseEntity<ApiResponseDTO<String>> uploadUserAvatar(@Valid @ModelAttribute AvatarRequestDTO request) {
        String imageUrl = imageService.uploadAvatar(request, "avatars");
        return ResponseEntity.ok(new ApiResponseDTO<String>(false, "Upload realizado com sucesso", imageUrl));
    }

    @PostMapping("upload")
    public ResponseEntity<ApiResponseDTO<String>> uploadGeneric(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = imageService.upload(file, "auctions"); 
            return ResponseEntity.ok(ApiResponseDTO.sucesso("Upload realizado com sucesso", imageUrl));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponseDTO.erro("Erro ao fazer upload: " + e.getMessage()));
        }
    }
}
