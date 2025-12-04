package com.github.luangust4vo.pw_leilao_backend.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.github.luangust4vo.pw_leilao_backend.dto.AuctionRequestDTO;
import com.github.luangust4vo.pw_leilao_backend.dto.AuctionResponseDTO; // Importe o DTO
import com.github.luangust4vo.pw_leilao_backend.exception.BusinessException;
import com.github.luangust4vo.pw_leilao_backend.exception.NotFoundException;
import com.github.luangust4vo.pw_leilao_backend.models.Auction;
import com.github.luangust4vo.pw_leilao_backend.models.Category;
import com.github.luangust4vo.pw_leilao_backend.models.Image;
import com.github.luangust4vo.pw_leilao_backend.models.Person;
import com.github.luangust4vo.pw_leilao_backend.models.enums.AuctionStatus;
import com.github.luangust4vo.pw_leilao_backend.repositories.AuctionRepository;

@Service
public class AuctionService {
    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private MessageSource messageSource;

    public Page<AuctionResponseDTO> findAllPublic(Pageable pageable) {
        return auctionRepository.findAll(pageable).map(AuctionResponseDTO::fromEntity);
    }

    public Page<AuctionResponseDTO> findAllBySeller(Person seller, Pageable pageable) {
        return auctionRepository.findBySellerId(seller.getId(), pageable).map(AuctionResponseDTO::fromEntity);
    }

    public AuctionResponseDTO findById(Long id) {
        Auction auction = auctionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("auction.not-found", new Object[] { id },
                                LocaleContextHolder.getLocale())));
        return AuctionResponseDTO.fromEntity(auction);
    }

    public AuctionResponseDTO create(AuctionRequestDTO dto, Person seller) {
        if (dto.getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException(messageSource.getMessage("auction.start-date-in-past", null, LocaleContextHolder.getLocale()));
        }
        if (dto.getStartDateTime().isAfter(dto.getEndDateTime())) {
            throw new BusinessException(messageSource.getMessage("auction.start-date-after-end-date", null, LocaleContextHolder.getLocale()));
        }
        
        Category category = categoryService.findById(dto.getCategoryId());

        Auction auction = new Auction();
        mapDtoToEntity(dto, auction);
        auction.setCategory(category);
        auction.setSeller(seller);
        auction.setStatus(AuctionStatus.IN_REVIEW);

        if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
            List<Image> images = dto.getImageUrls().stream().map(url -> {
                Image img = new Image();
                img.setUrl(url);
                img.setAuction(auction);
                return img;
            }).collect(Collectors.toList());
            auction.setImages(images);
        }

        return AuctionResponseDTO.fromEntity(auctionRepository.save(auction));
    }

    public AuctionResponseDTO update(Long id, AuctionRequestDTO dto, Person user) {
        Auction existingAuction = auctionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Leilão não encontrado"));

        if (!existingAuction.getSeller().getId().equals(user.getId())) {
             throw new BusinessException("Você não tem permissão para editar este leilão.");
        }
        if (existingAuction.getStatus() != AuctionStatus.IN_REVIEW && existingAuction.getStatus() != AuctionStatus.OPENED) {
             throw new BusinessException("Não é possível editar leilão fechado.");
        }

        Category category = categoryService.findById(dto.getCategoryId());
        mapDtoToEntity(dto, existingAuction);
        existingAuction.setCategory(category);
        
        if (dto.getImageUrls() != null) {
            existingAuction.getImages().clear();
            List<Image> newImages = dto.getImageUrls().stream().map(url -> {
                Image img = new Image();
                img.setUrl(url);
                img.setAuction(existingAuction);
                return img;
            }).collect(Collectors.toList());
            existingAuction.getImages().addAll(newImages);
        }

        return AuctionResponseDTO.fromEntity(auctionRepository.save(existingAuction));
    }

    public void delete(Long id, Person user) {
        Auction auction = auctionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Leilão não encontrado"));

        if (!auction.getSeller().getId().equals(user.getId())) {
            throw new BusinessException("Você não tem permissão para excluir este leilão.");
        }
        auctionRepository.delete(auction);
    }
    
    private void mapDtoToEntity(AuctionRequestDTO dto, Auction auction) {
        auction.setTitle(dto.getTitle());
        auction.setDescription(dto.getDescription());
        auction.setDetailedDescription(dto.getDetailedDescription());
        auction.setStartDateTime(dto.getStartDateTime());
        auction.setEndDateTime(dto.getEndDateTime());
        auction.setIncrementValue(dto.getIncrementValue());
        auction.setMinimumBid(dto.getMinimumBid());
        auction.setObservation(dto.getObservation());
    }
}