package com.github.luangust4vo.pw_leilao_backend.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.luangust4vo.pw_leilao_backend.dto.BidRequestDTO;
import com.github.luangust4vo.pw_leilao_backend.exception.BusinessException;
import com.github.luangust4vo.pw_leilao_backend.exception.NotFoundException;
import com.github.luangust4vo.pw_leilao_backend.models.Auction;
import com.github.luangust4vo.pw_leilao_backend.models.Bid;
import com.github.luangust4vo.pw_leilao_backend.models.Person;
import com.github.luangust4vo.pw_leilao_backend.models.enums.AuctionStatus;
import com.github.luangust4vo.pw_leilao_backend.repositories.AuctionRepository;
import com.github.luangust4vo.pw_leilao_backend.repositories.BidRepository;

@Service
public class BidService {
    @Autowired
    private BidRepository bidRepository;
    @Autowired
    private AuctionRepository auctionRepository;

    public void create(BidRequestDTO dto, Person bidder) {
        Auction auction = auctionRepository.findById(dto.getAuctionId())
                .orElseThrow(() -> new NotFoundException("Leilão não encontrado"));

        if (auction.getStatus() != AuctionStatus.OPENED) {
            throw new BusinessException("Este leilão não está aberto para lances.");
        }
        if (LocalDateTime.now().isAfter(auction.getEndDateTime())) {
            throw new BusinessException("O leilão já encerrou.");
        }
        if (auction.getSeller().getId().equals(bidder.getId())) {
            throw new BusinessException("Você não pode dar lances no seu próprio leilão.");
        }

        Optional<Bid> lastBidOpt = bidRepository.findTopByAuctionIdOrderByAmountDesc(auction.getId());
        if (lastBidOpt.isPresent() && lastBidOpt.get().getBidder().getId().equals(bidder.getId())) {
            throw new BusinessException("Você já é o detentor do maior lance.");
        }

        if (dto.getAmount().compareTo(auction.getMinimumBid()) < 0) {
             throw new BusinessException("O lance deve ser de pelo menos " + auction.getMinimumBid());
        }

        Bid bid = new Bid();
        bid.setAuction(auction);
        bid.setBidder(bidder);
        bid.setAmount(dto.getAmount());
        bid.setBidTime(LocalDateTime.now());
        
        bidRepository.save(bid);
        
        BigDecimal nextMandatoryBid = dto.getAmount().add(auction.getIncrementValue());
        auction.setMinimumBid(nextMandatoryBid);
        
        auctionRepository.save(auction);
    }
}