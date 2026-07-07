package com.alura.literalura.service;

import com.alura.literalura.exception.BusinessRuleException;
import com.alura.literalura.model.AppUser;
import com.alura.literalura.model.Member;
import com.alura.literalura.repository.AppUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final AppUserRepository userRepository;

    public CurrentUserService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** Socio asociado al usuario CLIENTE autenticado; falla si el usuario no es un cliente. */
    public Member currentMember(Authentication authentication) {
        AppUser user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new BusinessRuleException("Usuario no encontrado."));
        Member member = user.getMember();
        if (member == null) {
            throw new BusinessRuleException("El usuario autenticado no es un socio.");
        }
        return member;
    }
}
