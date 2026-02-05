package com.mini_crm.main.service;

import com.mini_crm.main.model.Customer;
import com.mini_crm.main.repository.CustomerRepository;
import com.mini_crm.main.dto.event.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@org.springframework.transaction.annotation.Transactional
public class CustomerService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    // Create
    public Customer createCustomer(Customer customer) {
        Customer savedCustomer = customerRepository.save(customer);
        if (savedCustomer.getSale() != null) {
            eventPublisher.publishEvent(new CustomerCreated(savedCustomer));
        }
        return savedCustomer;
    }

    // Read all with filter, sort, pagination
    public org.springframework.data.domain.Page<Customer> getCustomers(
            String search,
            Long saleId,
            int page,
            int size,
            String sortBy,
            String sortDir) {
        org.springframework.data.domain.Sort sort = sortDir.equalsIgnoreCase("desc")
                ? org.springframework.data.domain.Sort.by(sortBy).descending()
                : org.springframework.data.domain.Sort.by(sortBy).ascending();

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size,
                sort);

        org.springframework.data.jpa.domain.Specification<Customer> spec = (root, query, criteriaBuilder) -> {
            java.util.List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();

            if (search != null && !search.isEmpty()) {
                String searchLike = "%" + search.toLowerCase() + "%";
                jakarta.persistence.criteria.Predicate namePredicate = criteriaBuilder
                        .like(criteriaBuilder.lower(root.get("name")), searchLike);
                jakarta.persistence.criteria.Predicate emailPredicate = criteriaBuilder
                        .like(criteriaBuilder.lower(root.get("email")), searchLike);
                jakarta.persistence.criteria.Predicate phonePredicate = criteriaBuilder
                        .like(criteriaBuilder.lower(root.get("phone")), searchLike);
                jakarta.persistence.criteria.Predicate companyPredicate = criteriaBuilder
                        .like(criteriaBuilder.lower(root.get("company")), searchLike);

                predicates.add(criteriaBuilder.or(namePredicate, emailPredicate, phonePredicate, companyPredicate));
            }
            if (saleId != null) {
                predicates.add(criteriaBuilder.equal(root.get("sale").get("id"), saleId));
            }

            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        return customerRepository.findAll(spec, pageable);
    }

    // Read by ID
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    // Update
    public Customer updateCustomer(Long id, Customer customerDetails) {
        Optional<Customer> customerOptional = customerRepository.findById(id);
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            boolean saleChanged = false;
            if (customer.getSale() != null && customerDetails.getSale() != null
                    && customerDetails.getSale().getId() != customer.getSale().getId()) {
                logger.info("Customer sale changed from {} to {}", customer.getSale().getId(),
                        customerDetails.getSale().getId());
                saleChanged = true;
            } else if (customer.getSale() == null && customerDetails.getSale() != null) {
                saleChanged = true;
            }

            customer.setName(customerDetails.getName());
            customer.setPhone(customerDetails.getPhone());
            customer.setEmail(customerDetails.getEmail());
            customer.setCompany(customerDetails.getCompany());
            customer.setNotes(customerDetails.getNotes());
            customer.setSale(customerDetails.getSale());

            Customer savedCustomer = customerRepository.save(customer);

            if (saleChanged) {
                eventPublisher.publishEvent(new CustomerAssigned(savedCustomer));
            }

            return savedCustomer;
        }
        return null;
    }

    // Delete
    public boolean deleteCustomer(Long id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public Optional<Customer> findByPhone(String phone) {
        return customerRepository.findByPhone(phone);
    }
}
