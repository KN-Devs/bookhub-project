package fr.bookhub.service;



import fr.bookhub.bo.Categories;
import fr.bookhub.dal.CategoriesRepository;
import fr.bookhub.dto.CategoriesResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class CategoriesService {

    private final CategoriesRepository categoriesRepository;

    public CategoriesService(CategoriesRepository categoriesRepository) {
        this.categoriesRepository = categoriesRepository;
    }


    public List<CategoriesResponseDTO> getAllCategories() {
        return categoriesRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private CategoriesResponseDTO toDTO(Categories category) {
        CategoriesResponseDTO dto = new CategoriesResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }

}
