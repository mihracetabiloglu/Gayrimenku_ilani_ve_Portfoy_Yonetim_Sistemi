package com.gayrimenkul.system.service;
import com.gayrimenkul.system.entity.Category;
import com.gayrimenkul.system.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    // Tüm kategorileri listele
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }   
    // Yeni kategori ekle
    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }
    // Kategori güncelle
    public Category updateCategory(Long id, Category updatedCategory) {
        return categoryRepository.findById(id)
                .map(category -> {
                    category.setName(updatedCategory.getName());
                    return categoryRepository.save(category);
                })
                .orElseThrow(() -> new RuntimeException("Kategori bulunamadı"));
    }
    // Kategori sil
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }   
    // ID'ye göre kategori getir
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kategori bulunamadı"));
    }
}
