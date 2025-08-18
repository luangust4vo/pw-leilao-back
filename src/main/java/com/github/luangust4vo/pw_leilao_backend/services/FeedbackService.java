package com.github.luangust4vo.pw_leilao_backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.github.luangust4vo.pw_leilao_backend.exception.NotFoundException;
import com.github.luangust4vo.pw_leilao_backend.models.Auction;
import com.github.luangust4vo.pw_leilao_backend.models.Feedback;
import com.github.luangust4vo.pw_leilao_backend.models.Person;
import com.github.luangust4vo.pw_leilao_backend.repositories.FeedbackRepository;

@Service
public class FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private MessageSource messageSource;

    public List<Feedback> findAll() {
        return feedbackRepository.findAll();
    }

    public Page<Feedback> findAll(Pageable pageable) {
        return feedbackRepository.findAll(pageable);
    }

    public Feedback findById(Long id) {
        return feedbackRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("feedback.not-found", new Object[] { id },
                                LocaleContextHolder.getLocale())));
    }

    public List<Feedback> findByEvaluated(Person evaluated) {
        return feedbackRepository.findByEvaluated(evaluated);
    }

    public Page<Feedback> findByEvaluated(Person evaluated, Pageable pageable) {
        return feedbackRepository.findByEvaluated(evaluated, pageable);
    }

    public List<Feedback> findByAuction(Auction auction) {
        return feedbackRepository.findByAuction(auction);
    }

    public List<Feedback> findByEvaluator(Person evaluator) {
        return feedbackRepository.findByEvaluator(evaluator);
    }

    public Feedback create(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    public Feedback update(Long id, Feedback feedbackData) {
        Feedback existingFeedback = this.findById(id);
        
        existingFeedback.setRating(feedbackData.getRating());
        existingFeedback.setComment(feedbackData.getComment());
        
        return feedbackRepository.save(existingFeedback);
    }

    public void delete(Long id) {
        Feedback existingFeedback = this.findById(id);
        feedbackRepository.delete(existingFeedback);
    }
}
