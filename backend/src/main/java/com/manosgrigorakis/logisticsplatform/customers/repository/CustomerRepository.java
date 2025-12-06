package com.manosgrigorakis.logisticsplatform.customers.repository;

import com.manosgrigorakis.logisticsplatform.customers.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> , JpaSpecificationExecutor<Customer> {
    Optional<Customer> findByTin(String tin);

    Optional<Customer> findByCompanyName(String name);
}
