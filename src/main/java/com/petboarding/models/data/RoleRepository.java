package com.petboarding.models.data;

import com.petboarding.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JPARepositoryActiveFiltering<Role, Integer> {
    Role findByName(String name);
}
