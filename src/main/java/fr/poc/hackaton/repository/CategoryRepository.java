package fr.poc.hackaton.repository;

import fr.poc.hackaton.business.dto.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String> {

    Category findByInsightsCategoryId(String id);
}
