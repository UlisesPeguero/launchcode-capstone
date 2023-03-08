package com.petboarding.models.data;

import com.petboarding.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JPARepositoryActiveFiltering<User, Integer> {
    User findByUsername(String username);

    User findByResetPasswordToken(String resetPasswordToken);
}
