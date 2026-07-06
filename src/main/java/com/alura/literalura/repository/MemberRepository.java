package com.alura.literalura.repository;

import com.alura.literalura.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);

    boolean existsByDocumentId(String documentId);

    Optional<Member> findByEmail(String email);
}
