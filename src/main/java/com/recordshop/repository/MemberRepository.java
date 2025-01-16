package com.recordshop.repository;

import com.recordshop.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {


    Member findByPhoneNumber(String phoneNumber);

    Member findByUsername(String username);




}
