package com.github.luangust4vo.pw_leilao_backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.luangust4vo.pw_leilao_backend.models.Auction;
import com.github.luangust4vo.pw_leilao_backend.models.Feedback;
import com.github.luangust4vo.pw_leilao_backend.models.Person;
import com.github.luangust4vo.pw_leilao_backend.services.AuctionService;
import com.github.luangust4vo.pw_leilao_backend.services.FeedbackService;
import com.github.luangust4vo.pw_leilao_backend.services.PersonService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/feedbacks")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private PersonService personService;
    @Autowired
    private AuctionService auctionService;

    @GetMapping
    public ResponseEntity<Page<Feedback>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Feedback> feedbacks = feedbackService.findAll(pageable);
        return ResponseEntity.ok(feedbacks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Feedback> findById(@PathVariable Long id) {
        Feedback feedback = feedbackService.findById(id);
        return ResponseEntity.ok(feedback);
    }

    @GetMapping("/evaluated/{personId}")
    public ResponseEntity<Page<Feedback>> findByEvaluated(
            @PathVariable Long personId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Person person = personService.findById(personId);
        Pageable pageable = PageRequest.of(page, size);
        Page<Feedback> feedbacks = feedbackService.findByEvaluated(person, pageable);
        return ResponseEntity.ok(feedbacks);
    }

    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<List<Feedback>> findByAuction(@PathVariable Long auctionId) {
        Auction auction = auctionService.findById(auctionId);
        List<Feedback> feedbacks = feedbackService.findByAuction(auction);
        return ResponseEntity.ok(feedbacks);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Feedback> create(@Valid @RequestBody Feedback feedback) {
        Feedback createdFeedback = feedbackService.create(feedback);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFeedback);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Feedback> update(@PathVariable Long id, @Valid @RequestBody Feedback feedback) {
        Feedback updatedFeedback = feedbackService.update(id, feedback);
        return ResponseEntity.ok(updatedFeedback);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        feedbackService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
