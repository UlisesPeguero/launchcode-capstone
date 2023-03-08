package com.petboarding.models.data;

import com.petboarding.models.Configuration;

public interface ConfigurationRepository extends JPARepositoryActiveFiltering<Configuration, Integer> {

    public Configuration findByName(String name);
}
