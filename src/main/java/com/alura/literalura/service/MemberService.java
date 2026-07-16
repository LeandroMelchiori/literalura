package com.alura.literalura.service;

import com.alura.literalura.dto.MemberDTO;
import com.alura.literalura.dto.MemberRequest;
import com.alura.literalura.exception.BusinessRuleException;
import com.alura.literalura.exception.ResourceNotFoundException;
import com.alura.literalura.model.AppUser;
import com.alura.literalura.model.Member;
import com.alura.literalura.model.MemberStatus;
import com.alura.literalura.model.UserRole;
import com.alura.literalura.repository.AppUserRepository;
import com.alura.literalura.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final MemberRepository repository;
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository repository,
                         AppUserRepository userRepository,
                         PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Da de alta un socio y su usuario CLIENTE. El personal valida a la persona
     * y define sus credenciales; el socio queda con acceso a su propio portal.
     */
    @Transactional
    public MemberDTO registrarSocio(MemberRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw new BusinessRuleException("Ya existe un socio con ese email.");
        }
        if (repository.existsByDocumentId(request.documentId())) {
            throw new BusinessRuleException("Ya existe un socio con ese documento.");
        }
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new BusinessRuleException("Ya existe un usuario con ese nombre.");
        }

        Member member = repository.save(
                new Member(request.name(), request.email(), request.documentId()));
        userRepository.save(new AppUser(
                request.username(),
                passwordEncoder.encode(request.password()),
                UserRole.CLIENTE,
                member));
        return toDto(member);
    }

    @Transactional(readOnly = true)
    public Page<MemberDTO> obtenerSocios(String search, Pageable pageable) {
        Page<Member> members = (search != null && !search.isBlank())
                ? repository.findByNameContainingIgnoreCase(search.trim(), pageable)
                : repository.findAll(pageable);
        return members.map(this::toDto);
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
