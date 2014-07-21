package com.chessix.vas.actors.messages;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.Date;


/**
 * @author Mark Wigmans
 */
public class JournalMessage {

    @ToString
    @EqualsAndHashCode
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public static final class ClasCreated {
        @Getter
        Date timestamp;
        @Getter
        String clasId;

        private ClasCreated(final ClasCreatedBuilder builder) {
            this.timestamp = builder.timestamp;
            this.clasId = builder.clasId;
        }
    }

    @ToString
    @EqualsAndHashCode
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public static final class AccountCreated {
        @Getter
        Date timestamp;
        @Getter
        String clasId;
        @Getter
        String accountId;

        private AccountCreated(final AccountCreatedBuilder builder) {
            this.timestamp = builder.timestamp;
            this.clasId = builder.clasId;
            this.accountId = builder.accountId;
        }
    }

    @ToString
    @EqualsAndHashCode
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public static final class Transfer {
        @Getter
        Date timestamp;
        @Getter
        String clasId;
        @Getter
        String fromAccountId;
        @Getter
        String toAccountId;
        @Getter
        int amount;

        private Transfer(final TransferBuilder builder) {
            this.timestamp = builder.timestamp;
            this.clasId = builder.clasId;
            this.fromAccountId = builder.fromAccountId;
            this.toAccountId = builder.toAccountId;
            this.amount = builder.amount;
        }
    }

    @ToString
    @EqualsAndHashCode
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public static final class Clean {
        @Getter
        Date timestamp;
        @Getter
        String clasId;

        private Clean(final CleanBuilder builder) {
            this.timestamp = builder.timestamp;
            this.clasId = builder.clasId;
        }
    }

    public static class ClasCreatedBuilder implements Builder<ClasCreated> {
        private String clasId;
        private Date timestamp;

        public ClasCreatedBuilder(final String clasId) {
            this.clasId = clasId;
            this.timestamp = new Date();
        }

        public ClasCreated build() {
            return new ClasCreated(this);
        }
    }

    public static class AccountCreatedBuilder implements Builder<AccountCreated> {
        private String clasId;
        private String accountId;
        private Date timestamp;

        public AccountCreatedBuilder(final String clasId, final String accountId) {
            this.clasId = clasId;
            this.accountId = accountId;
            this.timestamp = new Date();
        }

        public AccountCreated build() {
            return new AccountCreated(this);
        }
    }

    public static class TransferBuilder implements Builder<Transfer> {
        private String clasId;
        private String fromAccountId;
        private String toAccountId;
        private int amount;
        private Date timestamp;

        public TransferBuilder(final String clasId, final String fromAccountId, final String toAccountId, final int amount) {
            this.clasId = clasId;
            this.fromAccountId = fromAccountId;
            this.toAccountId = toAccountId;
            this.amount = amount;
            this.timestamp = new Date();
        }

        public Transfer build() {
            return new Transfer(this);
        }
    }

    public static class CleanBuilder implements Builder<Clean> {
        private String clasId;
        private Date timestamp;

        public CleanBuilder(final String clasId) {
            this.clasId = clasId;
            this.timestamp = new Date();
        }

        public Clean build() {
            return new Clean(this);
        }
    }
}
