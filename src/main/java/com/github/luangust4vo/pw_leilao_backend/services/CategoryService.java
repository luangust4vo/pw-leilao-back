package com.github.luangust4vo.pw_leilao_backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.github.luangust4vo.pw_leilao_backend.exception.NotFoundException;
import com.github.luangust4vo.pw_leilao_backend.models.Category;
import com.github.luangust4vo.pw_leilao_backend.repositories.CategoryRepository;

@Service
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private MessageSource messageSource;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("category.not-found", new Object[] { id },
                                LocaleContextHolder.getLocale())));
    }

    public Category create(Category category) {
        return categoryRepository.save(category);
    }

    public Category update(Long id, Category categoryData) {
        Category existingCategory = this.findById(id);
        
        existingCategory.setName(categoryData.getName());
        existingCategory.setObservation(categoryData.getObservation());
        
        return categoryRepository.save(existingCategory);
    }

    public void delete(Long id) {
        Category existingCategory = this.findById(id);
        categoryRepository.delete(existingCategory);
    }
}
