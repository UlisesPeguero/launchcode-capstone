package com.petboarding.models.data;

import com.petboarding.models.InvoiceDetail;
import com.petboarding.models.PetService;
import com.petboarding.models.Reservation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceDetailRepository extends JPARepositoryActiveFiltering<InvoiceDetail, Integer> {
    public List<InvoiceDetail> findByService(PetService petService);
}
