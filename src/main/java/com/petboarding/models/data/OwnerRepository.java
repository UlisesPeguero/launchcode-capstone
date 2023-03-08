package com.petboarding.models.data;

import com.petboarding.models.Owner;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface OwnerRepository extends JPARepositoryActiveFiltering<Owner, Integer> {
}