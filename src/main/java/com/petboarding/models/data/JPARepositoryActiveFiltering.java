package com.petboarding.models.data;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface JPARepositoryActiveFiltering<T, ID extends Serializable> extends JpaRepository<T, ID> {
    public List<T> findByActive(Boolean active);
    public List<T> findByActive(Boolean active, Sort sort);
}
