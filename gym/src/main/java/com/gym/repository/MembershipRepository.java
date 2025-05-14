package com.gym.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gym.model.Membership;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
}
