package secure.examples;

import org.springframework.web.util.HtmlUtils;

import java.time.LocalDateTime;

/**
 * Secure ticket handling example.
 *
 * Before:
 * - Subject and message were stored exactly as received.
 * - No length checks and no defensive encoding guidance.
 *
 * Improved:
 * - Request fields are validated for required content and size.
 * - Stored values are normalized and encoded for safer downstream rendering.
 */
public class SecureTicketHandlingExample {

    public Ticket createTicket(TicketRequest request) {
        validate(request);

        Ticket ticket = new Ticket();
        ticket.setCustomerId(request.getCustomerId());
        ticket.setSubject(safeText(request.getSubject(), 120));
        ticket.setMessage(safeText(request.getMessage(), 2000));
        ticket.setStatus("OPEN");
        ticket.setCreatedAt(LocalDateTime.now());
        return ticket;
    }

    private void validate(TicketRequest request) {
        if (request == null || request.getCustomerId() == null) {
            throw new IllegalArgumentException("Customer ID is required");
        }
        if (request.getSubject() == null || request.getSubject().isBlank()) {
            throw new IllegalArgumentException("Subject is required");
        }
        if (request.getMessage() == null || request.getMessage().isBlank()) {
            throw new IllegalArgumentException("Message is required");
        }
    }

    private String safeText(String value, int maxLength) {
        String trimmed = value.trim();
        if (trimmed.length() > maxLength) {
            throw new IllegalArgumentException("Field length exceeded");
        }
        return HtmlUtils.htmlEscape(trimmed);
    }

    public static class TicketRequest {
        private Long customerId;
        private String subject;
        private String message;

        public Long getCustomerId() {
            return customerId;
        }

        public void setCustomerId(Long customerId) {
            this.customerId = customerId;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class Ticket {
        private Long customerId;
        private String subject;
        private String message;
        private String status;
        private LocalDateTime createdAt;

        public Long getCustomerId() {
            return customerId;
        }

        public void setCustomerId(Long customerId) {
            this.customerId = customerId;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }
}
