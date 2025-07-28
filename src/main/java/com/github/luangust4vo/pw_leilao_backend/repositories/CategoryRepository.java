package com.github.luangust4vo.pw_leilao_backend.repositories;

import com.github.luangust4vo.pw_leilao_backend.models.Category;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}