package com.github.luangust4vo.pw_leilao_backend.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.github.luangust4vo.pw_leilao_backend.dto.BidResponseDTO;
import com.github.luangust4vo.pw_leilao_backend.exception.BusinessException;
import com.github.luangust4vo.pw_leilao_backend.exception.NotFoundException;
import com.github.luangust4vo.pw_leilao_backend.models.Auction;
import com.github.luangust4vo.pw_leilao_backend.models.Bid;
import com.github.luangust4vo.pw_leilao_backend.models.Person;
import com.github.luangust4vo.pw_leilao_backend.models.enums.AuctionStatus;
import com.github.luangust4vo.pw_leilao_backend.repositories.BidRepository;

@Service
public class BidService {
    @Autowired
    private BidRepository bidRepository;
    @Autowired
    private AuctionService auctionService;

    public Bid placeBid(Long auctionId, BigDecimal amount, Person bidder) {
        Auction auction = auctionService.findById(auctionId);
        
        validateBid(auction, amount, bidder);
        
        Bid bid = new Bid();
        bid.setAuction(auction);
        bid.setBidder(bidder);
        bid.setAmount(amount);
        
        return bidRepository.save(bid);
    }
    
    private void validateBid(Auction auction, BigDecimal amount, Person bidder) {
        if (auction.getStatus() != AuctionStatus.OPENED) {
            throw new BusinessException("Não é possível dar lance em leilão que não está aberto");
        }
        
        if (auction.getEndDateTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Este leilão já foi encerrado");
        }
        
        if (auction.getSeller().getId().equals(bidder.getId())) {
            throw new BusinessException("O vendedor não pode dar lance no próprio leilão");
        }
        
        Optional<BigDecimal> currentHighestBid = bidRepository.findMaxBidAmountByAuction(auction);
        
        BigDecimal minimumBid;
        if (currentHighestBid.isPresent()) {
            minimumBid = currentHighestBid.get().add(auction.getIncrementValue());
        } else {
            minimumBid = auction.getMinimumBid();
        }
        
        if (amount.compareTo(minimumBid) < 0) {
            throw new BusinessException(
                String.format("O lance deve ser de pelo menos R$ %.2f", minimumBid)
            );
        }
    }
    
    public List<Bid> findBidsByAuction(Long auctionId) {
        Auction auction = auctionService.findById(auctionId);
        return bidRepository.findByAuctionOrderByAmountDesc(auction);
    }
    
    public Page<Bid> findBidsByAuction(Long auctionId, Pageable pageable) {
        Auction auction = auctionService.findById(auctionId);
        return bidRepository.findByAuctionOrderByBidTimeDesc(auction, pageable);
    }
    
    public Page<Bid> findBidsByBidder(Person bidder, Pageable pageable) {
        return bidRepository.findByBidderOrderByBidTimeDesc(bidder, pageable);
    }
    
    public Optional<Bid> findHighestBid(Long auctionId) {
        Auction auction = auctionService.findById(auctionId);
        return bidRepository.findHighestBidByAuction(auction);
    }
    
    public long countBids(Long auctionId) {
        Auction auction = auctionService.findById(auctionId);
        return bidRepository.countByAuction(auction);
    }
    
    public BigDecimal getNextMinimumBid(Long auctionId) {
        Auction auction = auctionService.findById(auctionId);
        Optional<BigDecimal> currentHighestBid = bidRepository.findMaxBidAmountByAuction(auction);
        
        if (currentHighestBid.isPresent()) {
            return currentHighestBid.get().add(auction.getIncrementValue());
        } else {
            return auction.getMinimumBid();
        }
    }
    
    // Métodos que retornam DTOs para reduzir payload
    public Page<BidResponseDTO> findBidsByAuctionAsDTO(Long auctionId, Pageable pageable) {
        Auction auction = auctionService.findById(auctionId);
        Page<Bid> bids = bidRepository.findByAuctionOrderByBidTimeDesc(auction, pageable);
        return bids.map(this::convertToDTO);
    }
    
    public Page<BidResponseDTO> findBidsByBidderAsDTO(Person bidder, Pageable pageable) {
        Page<Bid> bids = bidRepository.findByBidderOrderByBidTimeDesc(bidder, pageable);
        return bids.map(this::convertToDTO);
    }
    
    public Optional<BidResponseDTO> findHighestBidAsDTO(Long auctionId) {
        return findHighestBid(auctionId).map(this::convertToDTO);
    }
    
    private BidResponseDTO convertToDTO(Bid bid) {
        BidResponseDTO.BidderInfo bidderInfo = new BidResponseDTO.BidderInfo(
            bid.getBidder().getId(),
            bid.getBidder().getName()
        );
        
        return new BidResponseDTO(
            bid.getId(),
            bid.getAmount(),
            bid.getBidTime(),
            bidderInfo
        );
    }
    
    public Bid updateBid(Long bidId, BigDecimal newAmount, Person bidder) {
        Bid bid = bidRepository.findById(bidId)
            .orElseThrow(() -> new NotFoundException("Lance não encontrado"));
        
        // Verificar se o lance pertence ao usuário
        if (!bid.getBidder().getId().equals(bidder.getId())) {
            throw new BusinessException("Você só pode atualizar seus próprios lances");
        }
        
        // Verificar se o leilão ainda está aberto
        if (bid.getAuction().getStatus() != AuctionStatus.OPENED) {
            throw new BusinessException("Não é possível atualizar lance de leilão fechado");
        }
        
        // Verificar se o leilão ainda não expirou
        if (bid.getAuction().getEndDateTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Não é possível atualizar lance de leilão expirado");
        }
        
        // Verificar se o novo valor é válido
        validateBid(bid.getAuction(), newAmount, bidder);
        
        bid.setAmount(newAmount);
        bid.setBidTime(LocalDateTime.now());
        
        return bidRepository.save(bid);
    }
    
    public void deleteBid(Long bidId, Person bidder) {
        Bid bid = bidRepository.findById(bidId)
            .orElseThrow(() -> new NotFoundException("Lance não encontrado"));
        
        // Verificar se o lance pertence ao usuário
        if (!bid.getBidder().getId().equals(bidder.getId())) {
            throw new BusinessException("Você só pode excluir seus próprios lances");
        }
        
        // Verificar se o leilão ainda está aberto
        if (bid.getAuction().getStatus() != AuctionStatus.OPENED) {
            throw new BusinessException("Não é possível excluir lance de leilão fechado");
        }
        
        // Verificar se o leilão ainda não expirou
        if (bid.getAuction().getEndDateTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Não é possível excluir lance de leilão expirado");
        }
        
        // Verificar se não é o maior lance (opcional - pode ser uma regra de negócio)
        Optional<Bid> highestBid = findHighestBid(bid.getAuction().getId());
        if (highestBid.isPresent() && highestBid.get().getId().equals(bidId)) {
            throw new BusinessException("Não é possível excluir o maior lance atual");
        }
        
        bidRepository.delete(bid);
    }
}
