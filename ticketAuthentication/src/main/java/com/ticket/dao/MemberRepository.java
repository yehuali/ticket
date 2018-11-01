package com.ticket.dao;

import com.ticket.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findOneByUsername(String username);
}
