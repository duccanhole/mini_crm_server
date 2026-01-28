package com.mini_crm.main.repository;

import com.mini_crm.main.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>,
                org.springframework.data.jpa.repository.JpaSpecificationExecutor<Customer> {
        java.util.Optional<Customer> findByEmail(String email);

        java.util.Optional<Customer> findByPhone(String phone);
}
