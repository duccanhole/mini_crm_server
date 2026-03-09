package com.mini_crm.main.service;

import com.mini_crm.main.dto.event.CustomerAssigned;
import com.mini_crm.main.dto.event.CustomerCreated;
import com.mini_crm.main.model.Customer;
import com.mini_crm.main.model.User;
import com.mini_crm.main.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void createCustomer_withSale_publishesCustomerCreatedEvent() {
        // Given
        Customer customer = new Customer();
        User sale = new User();
        sale.setId(10L);
        customer.setSale(sale);

        when(customerRepository.save(customer)).thenReturn(customer);

        // When
        Customer result = customerService.createCustomer(customer);

        // Then
        assertEquals(customer, result);
        // Capture ApplicationEvent to verify the exact event payload.
        ArgumentCaptor<ApplicationEvent> eventCaptor = ArgumentCaptor.forClass(ApplicationEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertTrue(eventCaptor.getValue() instanceof CustomerCreated);
        assertEquals(customer, ((CustomerCreated) eventCaptor.getValue()).getCustomer());
    }

    @Test
    void createCustomer_withoutSale_doesNotPublishEvent() {
        // Given
        Customer customer = new Customer();
        customer.setSale(null);
        when(customerRepository.save(customer)).thenReturn(customer);

        // When
        Customer result = customerService.createCustomer(customer);

        // Then
        assertEquals(customer, result);
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void updateCustomer_whenSaleChanged_publishesCustomerAssignedEvent() {
        // Given: existing customer already has an assigned sale.
        User oldSale = new User();
        oldSale.setId(1L);
        Customer existing = new Customer();
        existing.setId(100L);
        existing.setSale(oldSale);
        existing.setName("Old");
        existing.setPhone("111");
        existing.setEmail("old@mail.com");

        User newSale = new User();
        newSale.setId(2L);
        Customer details = new Customer();
        details.setName("New");
        details.setPhone("222");
        details.setEmail("new@mail.com");
        details.setCompany("Acme");
        details.setNotes("Updated");
        details.setSale(newSale);

        when(customerRepository.findById(100L)).thenReturn(Optional.of(existing));
        when(customerRepository.save(existing)).thenReturn(existing);

        // When
        Customer result = customerService.updateCustomer(100L, details);

        // Then
        assertEquals(existing, result);
        assertEquals("New", existing.getName());
        assertEquals("222", existing.getPhone());
        assertEquals("new@mail.com", existing.getEmail());
        assertEquals(newSale, existing.getSale());

        ArgumentCaptor<ApplicationEvent> eventCaptor = ArgumentCaptor.forClass(ApplicationEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertTrue(eventCaptor.getValue() instanceof CustomerAssigned);
        assertEquals(existing, ((CustomerAssigned) eventCaptor.getValue()).getCustomer());
    }

    @Test
    void updateCustomer_whenNotFound_returnsNull() {
        // Given
        Customer details = new Customer();
        when(customerRepository.findById(404L)).thenReturn(Optional.empty());

        // When
        Customer result = customerService.updateCustomer(404L, details);

        // Then
        assertNull(result);
        verify(customerRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void deleteCustomer_whenNotExists_returnsFalseAndDoesNotDelete() {
        // Given
        when(customerRepository.existsById(99L)).thenReturn(false);

        // When
        boolean result = customerService.deleteCustomer(99L);

        // Then
        assertFalse(result);
        verify(customerRepository, never()).deleteById(any());
    }
}
