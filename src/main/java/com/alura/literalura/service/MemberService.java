package com.alura.literalura.service;

import com.alura.literalura.dto.MemberDTO;
import com.alura.literalura.dto.MemberRequest;
import com.alura.literalura.exception.BusinessRuleException;
import com.alura.literalura.exception.ResourceNotFoundException;
import com.alura.literalura.model.Member;
import com.alura.literalura.model.MemberStatus;
import com.alura.literalura.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final MemberRepository repository;

    public MemberService(MemberRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public MemberDTO registrarSocio(MemberRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw new BusinessRuleException("Ya existe un socio con ese email.");
        }
        if (repository.existsByDocumentId(request.documentId())) {
            throw new BusinessRuleException("Ya existe un socio con ese documento.");
        }
        Member member = repository.save(
                new Member(request.name(), request.email(), request.documentId()));
        return toDto(member);
    }

    @Transactional(readOnly = true)
    public Page<MemberDTO> obtenerSocios(Pageable pageable) {
        return repository.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public MemberDTO obtenerSocio(Long id) {
        return toDto(buscarOFallar(id));
    }

    @Transactional
    public MemberDTO cambiarEstado(Long id, MemberStatus status) {
        Member member = buscarOFallar(id);
        member.setStatus(status);
        return toDto(member);
    }

    private Member buscarOFallar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Socio", id));
    }

    private MemberDTO toDto(Member m) {
        return new MemberDTO(m.getId(), m.getName(), m.getEmail(),
                m.getDocumentId(), m.getStatus(), m.getRegisteredAt());
    }
}
