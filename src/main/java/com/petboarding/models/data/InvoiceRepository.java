package com.petboarding.models.data;

import com.petboarding.models.Invoice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;

@Repository
public interface InvoiceRepository extends JPARepositoryActiveFiltering<Invoice, Integer>{

    @Query( value = "SELECT MAX(i.number) + 1 FROM Invoice i WHERE YEAR(i.date) = YEAR(:date)", nativeQuery = true)
    public BigDecimal findNextNumberByDate(@Param("date") Date date);
}
