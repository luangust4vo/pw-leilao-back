package com.github.luangust4vo.pw_leilao_backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.luangust4vo.pw_leilao_backend.models.Auction;
import com.github.luangust4vo.pw_leilao_backend.models.Payment;
import com.github.luangust4vo.pw_leilao_backend.models.Person;
import com.github.luangust4vo.pw_leilao_backend.models.enums.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByAuction(Auction auction);
    List<Payment> findByBuyer(Person buyer);
    List<Payment> findByStatus(PaymentStatus status);
    Optional<Payment> findByTransactionId(String transactionId);
}
