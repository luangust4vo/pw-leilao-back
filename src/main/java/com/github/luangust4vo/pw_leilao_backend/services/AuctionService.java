package com.github.luangust4vo.pw_leilao_backend.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.github.luangust4vo.pw_leilao_backend.exception.BusinessException;
import com.github.luangust4vo.pw_leilao_backend.exception.NotFoundException;
import com.github.luangust4vo.pw_leilao_backend.models.Auction;
import com.github.luangust4vo.pw_leilao_backend.models.Category;
import com.github.luangust4vo.pw_leilao_backend.models.Person;
import com.github.luangust4vo.pw_leilao_backend.models.enums.AuctionStatus;
import com.github.luangust4vo.pw_leilao_backend.repositories.AuctionRepository;

@Service
public class AuctionService {
    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private CategoryService categoryService;

    public List<Auction> findAll() {
        return auctionRepository.findAll();
    }

    public Page<Auction> findAll(Pageable pageable) {
        return auctionRepository.findAll(pageable);
    }

    public Auction findById(Long id) {
        return auctionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("auction.not-found", new Object[] { id },
                                LocaleContextHolder.getLocale())));
    }

    public Auction create(Auction auction) {
        validateAuction(auction);
        auction.setStatus(AuctionStatus.IN_REVIEW); 
        return auctionRepository.save(auction);
    }

    public Auction update(Long id, Auction auctionData) {
        Auction existingAuction = this.findById(id);
        if (existingAuction.getStatus() == AuctionStatus.OPENED) {
            throw new BusinessException(
                messageSource.getMessage("auction.cannot-edit-opened", null,
                        LocaleContextHolder.getLocale()));
        }
        
        if (existingAuction.getStatus() == AuctionStatus.CLOSED) {
            throw new BusinessException(
                messageSource.getMessage("auction.cannot-edit-closed", null,
                        LocaleContextHolder.getLocale()));
        }
        
        existingAuction.setTitle(auctionData.getTitle());
        existingAuction.setDescription(auctionData.getDescription());
        existingAuction.setDetailedDescription(auctionData.getDetailedDescription());
        existingAuction.setStartDateTime(auctionData.getStartDateTime());
        existingAuction.setEndDateTime(auctionData.getEndDateTime());
        existingAuction.setObservation(auctionData.getObservation());
        existingAuction.setIncrementValue(auctionData.getIncrementValue());
        existingAuction.setMinimumBid(auctionData.getMinimumBid());
        existingAuction.setCategory(auctionData.getCategory());
        
        validateAuction(existingAuction);
        return auctionRepository.save(existingAuction);
    }

    public void delete(Long id) {
        Auction existingAuction = this.findById(id);
        if (existingAuction.getStatus() == AuctionStatus.OPENED) {
            throw new BusinessException(
                messageSource.getMessage("auction.cannot-delete-opened", null,
                        LocaleContextHolder.getLocale()));
        }
        
        if (existingAuction.getStatus() == AuctionStatus.CLOSED) {
            throw new BusinessException(
                messageSource.getMessage("auction.cannot-delete-closed", null,
                        LocaleContextHolder.getLocale()));
        }
        
        auctionRepository.delete(existingAuction);
    }

    public Page<Auction> findOpenAuctions(Pageable pageable) {
        return auctionRepository.findOpenAuctions(pageable);
    }

    public Page<Auction> findByCategory(Category category, Pageable pageable) {
        return auctionRepository.findByCategory(category, pageable);
    }

    public Page<Auction> findBySeller(Person seller, Pageable pageable) {
        return auctionRepository.findBySeller(seller, pageable);
    }

    public Page<Auction> findByStatus(AuctionStatus status, Pageable pageable) {
        return auctionRepository.findByStatus(status, pageable);
    }

    public Page<Auction> findByCategoryAndStatus(Category category, AuctionStatus status, Pageable pageable) {
        return auctionRepository.findByCategoryAndStatus(category, status, pageable);
    }

    public Auction approveAuction(Long id) {
        Auction auction = findById(id);
        
        if (auction.getStatus() != AuctionStatus.IN_REVIEW) {
            throw new BusinessException(
                messageSource.getMessage("auction.cannot-approve-not-in-review", null,
                        LocaleContextHolder.getLocale()));
        }
        
        auction.setStatus(AuctionStatus.OPENED);
        return auctionRepository.save(auction);
    }

    public Auction rejectAuction(Long id, String observation) {
        Auction auction = findById(id);
        if (auction.getStatus() != AuctionStatus.IN_REVIEW) {
            throw new BusinessException(
                messageSource.getMessage("auction.cannot-reject-not-in-review", null,
                        LocaleContextHolder.getLocale()));
        }
        
        auction.setStatus(AuctionStatus.CANCELED);
        if (observation != null && !observation.trim().isEmpty()) {
            auction.setObservation(auction.getObservation() + "\n[ADMIN] " + observation);
        }
        return auctionRepository.save(auction);
    }

    public Auction closeAuction(Long id) {
        Auction auction = findById(id);
        if (auction.getStatus() != AuctionStatus.OPENED) {
            throw new BusinessException(
                messageSource.getMessage("auction.cannot-close-not-opened", null,
                        LocaleContextHolder.getLocale()));
        }
        
        auction.setStatus(AuctionStatus.CLOSED);
        return auctionRepository.save(auction);
    }

    private void validateAuction(Auction auction) {
        if (auction.getStartDateTime() != null && auction.getEndDateTime() != null) {
            if (auction.getStartDateTime().isAfter(auction.getEndDateTime())) {
                throw new BusinessException(
                    messageSource.getMessage("auction.start-date-after-end-date", null,
                            LocaleContextHolder.getLocale()));
            }
            
            if (auction.getStartDateTime().isBefore(LocalDateTime.now())) {
                throw new BusinessException(
                    messageSource.getMessage("auction.start-date-in-past", null,
                            LocaleContextHolder.getLocale()));
            }
        }
        
        if (auction.getMinimumBid() != null && auction.getIncrementValue() != null) {
            if (auction.getIncrementValue() > auction.getMinimumBid()) {
                throw new BusinessException(
                    messageSource.getMessage("auction.increment-greater-than-minimum", null,
                            LocaleContextHolder.getLocale()));
            }
        }
        
        if (auction.getCategory() != null && auction.getCategory().getId() != null) {
            categoryService.findById(auction.getCategory().getId());
        }
    }
}
