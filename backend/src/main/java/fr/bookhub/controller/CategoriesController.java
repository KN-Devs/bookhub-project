package fr.bookhub.controller;


import fr.bookhub.dto.CategoriesResponseDTO;
import fr.bookhub.dto.LoansResponseDTO;
import fr.bookhub.service.CategoriesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoriesController {

    private final CategoriesService categoriesService;

    public CategoriesController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @GetMapping
    public ResponseEntity<List<CategoriesResponseDTO>> getAllCategories() {
        return ResponseEntity.ok(categoriesService.getAllCategories());
    }


}
