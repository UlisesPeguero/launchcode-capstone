package com.petboarding.models.data;

import com.petboarding.models.StayStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StayStatusRepository extends JpaRepository<StayStatus, Integer> {
}
