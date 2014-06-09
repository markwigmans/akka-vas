package com.chessix.vas.actors.messages;

import java.util.Date;

import lombok.Value;

/**
 * 
 * @author Mark Wigmans
 *
 */
public class JournalMessage {

    @Value
    public static class ClasCreated {
        String clasId;
    }

    @Value
    public static class AccountCreated {
        String clasId;
        String accountId;
    }

    @Value
    public static class Transfer {
        String clasId;
        String fromAccountId;
        String toAccountId;
        int amount;
        Date date;
    }

    @Value
    public static class Clean {
        String clasId;
    }
}
