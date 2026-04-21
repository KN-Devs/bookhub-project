package fr.bookhub.service;

import fr.bookhub.dto.LoansRequestDTO;
import fr.bookhub.dto.LoansResponseDTO;
import java.util.List;

public interface LoansService {

    LoansResponseDTO createLoan(LoansRequestDTO dto);
    LoansResponseDTO getLoanById(int id);
    List<LoansResponseDTO> getAllLoans();
    List<LoansResponseDTO> getLoansByUser(int userId);
    List<LoansResponseDTO> getLoansByStatus(String status);
    LoansResponseDTO returnBook(int id);
    void deleteLoan(int id);

   ;
}