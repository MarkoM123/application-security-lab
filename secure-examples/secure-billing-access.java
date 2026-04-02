package secure.examples;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Secure billing access example.
 *
 * Before:
 * - Billing records were returned by account ID with no policy enforcement.
 *
 * Improved:
 * - Billing access is granted only to owner or admin.
 * - Input type is numeric, reducing unsafe query usage.
 */
public class SecureBillingAccessExample {

    public BillingAccount getBillingAccount(long accountId, RequesterContext requester, BillingRepository repository) {
        BillingAccount account = repository.findByAccountId(accountId);
        if (account == null) {
            return null;
        }

        boolean isOwner = Objects.equals(account.getOwnerUserId(), requester.getUserId());
        boolean isAdmin = "ADMIN".equals(requester.getRole());
        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("Forbidden");
        }

        return account;
    }

    public interface BillingRepository {
        BillingAccount findByAccountId(long accountId);
    }

    public static class BillingAccount {
        private final Long ownerUserId;
        private final BigDecimal balance;

        public BillingAccount(Long ownerUserId, BigDecimal balance) {
            this.ownerUserId = ownerUserId;
            this.balance = balance;
        }

        public Long getOwnerUserId() {
            return ownerUserId;
        }

        public BigDecimal getBalance() {
            return balance;
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
