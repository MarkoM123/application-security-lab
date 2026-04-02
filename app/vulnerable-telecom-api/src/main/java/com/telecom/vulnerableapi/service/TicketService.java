package com.telecom.vulnerableapi.service;

import com.telecom.vulnerableapi.dto.TicketRequest;
import com.telecom.vulnerableapi.model.Ticket;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TicketService {

    private final List<Ticket> tickets = new ArrayList<>();
    private final AtomicLong idSequence = new AtomicLong(2000);

    public Ticket createTicket(TicketRequest request) {
        Ticket ticket = new Ticket();
        ticket.setId(idSequence.incrementAndGet());
        ticket.setCustomerId(request.getCustomerId());

        // VULNERABILITY: Missing input sanitization and output encoding for ticket fields.
        // VULNERABILITY: Stored/Reflected XSS risk because input is stored and later returned as-is.
        ticket.setSubject(request.getSubject());
        ticket.setMessage(request.getMessage());

        ticket.setStatus("OPEN");
        ticket.setCreatedAt(LocalDateTime.now());
        tickets.add(ticket);
        return ticket;
    }

    public List<Ticket> listTickets() {
        return tickets;
    }
}

