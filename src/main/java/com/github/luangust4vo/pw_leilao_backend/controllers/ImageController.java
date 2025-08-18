package com.github.luangust4vo.pw_leilao_backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RestController;

import com.github.luangust4vo.pw_leilao_backend.models.Auction;
import com.github.luangust4vo.pw_leilao_backend.models.Image;
import com.github.luangust4vo.pw_leilao_backend.services.AuctionService;
import com.github.luangust4vo.pw_leilao_backend.services.ImageService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/images")
public class ImageController {
    @Autowired
    private ImageService imageService;
    @Autowired
    private AuctionService auctionService;

    @GetMapping
    public ResponseEntity<List<Image>> findAll() {
        List<Image> images = imageService.findAll();
        return ResponseEntity.ok(images);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Image> findById(@PathVariable Long id) {
        Image image = imageService.findById(id);
        return ResponseEntity.ok(image);
    }

    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<List<Image>> findByAuction(@PathVariable Long auctionId) {
        Auction auction = auctionService.findById(auctionId);
        List<Image> images = imageService.findByAuction(auction);
        return ResponseEntity.ok(images);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Image> create(@Valid @RequestBody Image image) {
        Image createdImage = imageService.create(image);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdImage);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Image> update(@PathVariable Long id, @Valid @RequestBody Image image) {
        Image updatedImage = imageService.update(id, image);
        return ResponseEntity.ok(updatedImage);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        imageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
