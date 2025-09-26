package fr.poc.hackaton.service;

import fr.poc.hackaton.business.dto.Category;
import fr.poc.hackaton.business.usecase.CreateCategorie;
import fr.poc.hackaton.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CreateCategorie createCategorie;
    private final CategoryRepository categoryRepository;

    public synchronized Category getCategory(String id, String logoUrl, String name) {
        if(Objects.equals(id, "uncategorized")){
            return null;
        }
        Category category = this.categoryRepository.findByInsightsCategoryId(id);
        if (category == null) {
            category = this.categoryRepository.save(this.createCategorie.createCategory(id, logoUrl, name));
            System.out.println("Creating new category : " + id);
        }
        return category;
    }
}
