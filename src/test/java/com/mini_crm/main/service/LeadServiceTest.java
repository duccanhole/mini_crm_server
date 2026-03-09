package com.mini_crm.main.service;

import com.mini_crm.main.dto.event.LeadCreated;
import com.mini_crm.main.dto.event.LeadUpdated;
import com.mini_crm.main.model.Lead;
import com.mini_crm.main.model.User;
import com.mini_crm.main.repository.LeadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
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
class LeadServiceTest {

    @Mock
    private LeadRepository leadRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private LeadService leadService;

    @Test
    void createLead_withAssignedUserId_publishesLeadCreatedEvent() {
        // Given
        Lead lead = new Lead();
        User assignedTo = new User();
        assignedTo.setId(9L);
        lead.setAssignedTo(assignedTo);

        when(leadRepository.save(lead)).thenReturn(lead);

        // When
        Lead result = leadService.createLead(lead);

        // Then
        assertEquals(lead, result);
        // Use ApplicationEvent captor to avoid overload mismatch on publishEvent(...).
        ArgumentCaptor<ApplicationEvent> eventCaptor = ArgumentCaptor.forClass(ApplicationEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertTrue(eventCaptor.getValue() instanceof LeadCreated);
        assertEquals(lead, ((LeadCreated) eventCaptor.getValue()).getLead());
    }

    @Test
    void createLead_withoutAssignedUser_doesNotPublishEvent() {
        // Given
        Lead lead = new Lead();
        lead.setAssignedTo(null);
        when(leadRepository.save(lead)).thenReturn(lead);

        // When
        Lead result = leadService.createLead(lead);

        // Then
        assertEquals(lead, result);
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void updateLead_whenFoundAndCreatedByPresent_publishesLeadUpdatedEvent() {
        // Given
        Lead existingLead = new Lead();
        existingLead.setId(1L);

        User creator = new User();
        creator.setId(100L);

        User assignee = new User();
        assignee.setId(200L);

        Lead leadDetails = new Lead();
        leadDetails.setStatus("Contacted");
        leadDetails.setValue(5000.0);
        leadDetails.setAssignedTo(assignee);
        leadDetails.setCreatedBy(creator);

        User updatedBy = new User();
        updatedBy.setId(300L);

        when(leadRepository.findById(1L)).thenReturn(Optional.of(existingLead));
        when(leadRepository.save(existingLead)).thenReturn(existingLead);

        // When
        Lead result = leadService.updateLead(1L, leadDetails, updatedBy);

        // Then
        assertEquals(existingLead, result);
        assertEquals("Contacted", existingLead.getStatus());
        assertEquals(5000.0, existingLead.getValue());
        assertEquals(assignee, existingLead.getAssignedTo());
        assertEquals(creator, existingLead.getCreatedBy());

        ArgumentCaptor<ApplicationEvent> eventCaptor = ArgumentCaptor.forClass(ApplicationEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertTrue(eventCaptor.getValue() instanceof LeadUpdated);
        LeadUpdated event = (LeadUpdated) eventCaptor.getValue();
        assertEquals(existingLead, event.getLead());
        assertEquals(updatedBy, event.getUpdatedBy());
    }

    @Test
    void updateLead_whenNotFound_returnsNull() {
        // Given
        when(leadRepository.findById(404L)).thenReturn(Optional.empty());

        // When
        Lead result = leadService.updateLead(404L, new Lead(), new User());

        // Then
        assertNull(result);
        verify(leadRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void updateLead_whenCreatedByMissing_doesNotPublishEvent() {
        // Given
        Lead existingLead = new Lead();
        existingLead.setId(10L);
        Lead leadDetails = new Lead();
        leadDetails.setStatus("Won");
        leadDetails.setCreatedBy(null);

        when(leadRepository.findById(10L)).thenReturn(Optional.of(existingLead));
        when(leadRepository.save(existingLead)).thenReturn(existingLead);

        // When
        Lead result = leadService.updateLead(10L, leadDetails, new User());

        // Then
        assertEquals(existingLead, result);
        assertEquals("Won", existingLead.getStatus());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void deleteLead_whenExists_deletesAndReturnsTrue() {
        // Given
        when(leadRepository.existsById(7L)).thenReturn(true);

        // When
        boolean result = leadService.deleteLead(7L);

        // Then
        assertTrue(result);
        verify(leadRepository).deleteById(7L);
    }

    @Test
    void deleteLead_whenNotExists_returnsFalseAndDoesNotDelete() {
        // Given
        when(leadRepository.existsById(8L)).thenReturn(false);

        // When
        boolean result = leadService.deleteLead(8L);

        // Then
        assertFalse(result);
        verify(leadRepository, never()).deleteById(any());
    }

    @Test
    void getLeadValue_ignoresNullValuesAndSumsNonNullValues() {
        // Given: mixed values, null should be ignored.
        Lead lead1 = new Lead();
        lead1.setValue(100.0);
        Lead lead2 = new Lead();
        lead2.setValue(null);
        Lead lead3 = new Lead();
        lead3.setValue(250.5);
        when(leadRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
                .thenReturn(List.of(lead1, lead2, lead3));

        // When
        double total = leadService.getLeadValue(null, null, null, null, null);

        // Then
        assertEquals(350.5, total);
    }
}
