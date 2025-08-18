package com.github.luangust4vo.pw_leilao_backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.github.luangust4vo.pw_leilao_backend.exception.NotFoundException;
import com.github.luangust4vo.pw_leilao_backend.models.Auction;
import com.github.luangust4vo.pw_leilao_backend.models.Image;
import com.github.luangust4vo.pw_leilao_backend.repositories.ImageRepository;

@Service
public class ImageService {
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private MessageSource messageSource;

    public List<Image> findAll() {
        return imageRepository.findAll();
    }

    public Image findById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("image.not-found", new Object[] { id },
                                LocaleContextHolder.getLocale())));
    }

    public List<Image> findByAuction(Auction auction) {
        return imageRepository.findByAuction(auction);
    }

    public List<Image> findByAuctionId(Long auctionId) {
        return imageRepository.findByAuctionId(auctionId);
    }

    public Image create(Image image) {
        return imageRepository.save(image);
    }

    public Image update(Long id, Image imageData) {
        Image existingImage = this.findById(id);
        
        existingImage.setUrl(imageData.getUrl());
        existingImage.setDescription(imageData.getDescription());
        existingImage.setAuction(imageData.getAuction());
        
        return imageRepository.save(existingImage);
    }

    public void delete(Long id) {
        Image existingImage = this.findById(id);
        imageRepository.delete(existingImage);
    }
}
