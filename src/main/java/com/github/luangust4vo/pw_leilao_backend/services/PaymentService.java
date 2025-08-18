package com.github.luangust4vo.pw_leilao_backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.github.luangust4vo.pw_leilao_backend.exception.NotFoundException;
import com.github.luangust4vo.pw_leilao_backend.models.Auction;
import com.github.luangust4vo.pw_leilao_backend.models.Payment;
import com.github.luangust4vo.pw_leilao_backend.models.Person;
import com.github.luangust4vo.pw_leilao_backend.models.enums.PaymentStatus;
import com.github.luangust4vo.pw_leilao_backend.repositories.PaymentRepository;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private MessageSource messageSource;

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    public Payment findById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("payment.not-found", new Object[] { id },
                                LocaleContextHolder.getLocale())));
    }

    public Optional<Payment> findByAuction(Auction auction) {
        return paymentRepository.findByAuction(auction);
    }

    public List<Payment> findByBuyer(Person buyer) {
        return paymentRepository.findByBuyer(buyer);
    }

    public List<Payment> findByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }

    public Optional<Payment> findByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId);
    }

    public Payment create(Payment payment) {
        return paymentRepository.save(payment);
    }

    public Payment update(Long id, Payment paymentData) {
        Payment existingPayment = this.findById(id);
        
        existingPayment.setAmount(paymentData.getAmount());
        existingPayment.setStatus(paymentData.getStatus());
        existingPayment.setTransactionId(paymentData.getTransactionId());
        
        return paymentRepository.save(existingPayment);
    }

    public Payment updateStatus(Long id, PaymentStatus status) {
        Payment existingPayment = this.findById(id);
        existingPayment.setStatus(status);
        return paymentRepository.save(existingPayment);
    }

    public void delete(Long id) {
        Payment existingPayment = this.findById(id);
        paymentRepository.delete(existingPayment);
    }
}
