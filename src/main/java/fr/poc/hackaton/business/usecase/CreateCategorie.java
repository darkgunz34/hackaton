package fr.poc.hackaton.business.usecase;

import fr.poc.hackaton.business.dto.Category;
import org.springframework.stereotype.Service;

@Service
public class CreateCategorie {

    public Category createCategory(String id, String logoUrl, String name) {
        Category category = new Category();
        category.setInsightsCategoryId(id);
        category.setInsightsCategoryLogoUrl(logoUrl);
        category.setInsightsCategoryName(name);
        return category;
}
}
