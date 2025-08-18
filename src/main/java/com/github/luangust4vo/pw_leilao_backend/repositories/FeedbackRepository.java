package com.github.luangust4vo.pw_leilao_backend.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.github.luangust4vo.pw_leilao_backend.models.Auction;
import com.github.luangust4vo.pw_leilao_backend.models.Feedback;
import com.github.luangust4vo.pw_leilao_backend.models.Person;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByEvaluated(Person evaluated);
    Page<Feedback> findByEvaluated(Person evaluated, Pageable pageable);
    List<Feedback> findByAuction(Auction auction);
    List<Feedback> findByEvaluator(Person evaluator);
}
