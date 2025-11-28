package com.manosgrigorakis.logisticsplatform.repository;

import com.manosgrigorakis.logisticsplatform.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByTin(String tin);

    Optional<Customer> findByCompanyName(String name);
}
