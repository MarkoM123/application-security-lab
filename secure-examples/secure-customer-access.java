package secure.examples;

import java.util.Objects;

/**
 * Secure customer access example.
 *
 * Before:
 * - Customer object was returned directly by ID.
 * - No ownership verification was performed.
 *
 * Improved:
 * - Service enforces owner/admin authorization before returning data.
 */
public class SecureCustomerAccessExample {

    public Customer getCustomerById(long customerId, RequesterContext requester, CustomerRepository repository) {
        Customer customer = repository.findById(customerId);
        if (customer == null) {
            return null;
        }

        boolean isOwner = Objects.equals(customer.getOwnerUserId(), requester.getUserId());
        boolean isAdmin = "ADMIN".equals(requester.getRole());
        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("Forbidden");
        }

        return customer;
    }

    public interface CustomerRepository {
        Customer findById(long id);
    }

    public static class Customer {
        private final Long ownerUserId;

        public Customer(Long ownerUserId) {
            this.ownerUserId = ownerUserId;
        }

        public Long getOwnerUserId() {
            return ownerUserId;
        }
    }

    public static class RequesterContext {
        private final Long userId;
        private final String role;

        public RequesterContext(Long userId, String role) {
            this.userId = userId;
            this.role = role;
        }

        public Long getUserId() {
            return userId;
        }

        public String getRole() {
            return role;
        }
    }

    public static class AccessDeniedException extends RuntimeException {
        public AccessDeniedException(String message) {
            super(message);
        }
    }
}
