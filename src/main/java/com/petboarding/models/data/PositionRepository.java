package com.petboarding.models.data;

import com.petboarding.models.Position;
import org.springframework.data.repository.CrudRepository;

public interface PositionRepository extends JPARepositoryActiveFiltering<Position, Integer> {
}
