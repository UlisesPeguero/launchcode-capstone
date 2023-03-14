package com.petboarding.models.data;

import com.petboarding.models.Stay;
import org.springframework.stereotype.Repository;

@Repository
public interface StayRepository extends JPARepositoryActiveFiltering<Stay, Integer> {
}
