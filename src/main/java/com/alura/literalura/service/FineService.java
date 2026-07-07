package com.alura.literalura.service;

import com.alura.literalura.dto.FineDTO;
import com.alura.literalura.exception.BusinessRuleException;
import com.alura.literalura.exception.ResourceNotFoundException;
import com.alura.literalura.model.Fine;
import com.alura.literalura.repository.FineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FineService {

    private final FineRepository fineRepository;

    public FineService(FineRepository fineRepository) {
        this.fineRepository = fineRepository;
    }

    @Transactional(readOnly = true)
    public List<FineDTO> misMultas(Long memberId) {
        return fineRepository.findByMemberIdOrderByCreatedAtDesc(memberId)
                .stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<FineDTO> impagas() {
        return fineRepository.findByPaidFalseOrderByCreatedAtAsc()
                .stream().map(this::toDto).toList();
    }

    @Transactional
    public FineDTO registrarPago(Long fineId) {
        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new ResourceNotFoundException("Multa", fineId));
        if (fine.isPaid()) {
            throw new BusinessRuleException("La multa ya fue pagada.");
        }
        fine.pay();
        return toDto(fine);
    }

    private FineDTO toDto(Fine f) {
        return new FineDTO(
                f.getId(),
                f.getMember().getId(),
                f.getMember().getName(),
                f.getLoan().getId(),
                f.getLoan().getCopy().getBook().getTitle(),
                f.getAmount(),
                f.getDaysLate(),
                f.getCreatedAt(),
                f.isPaid());
    }
}
