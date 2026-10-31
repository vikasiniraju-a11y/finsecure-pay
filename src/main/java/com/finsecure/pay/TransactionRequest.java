package com.finsecure.pay;

import java.math.BigDecimal;

public record TransactionRequest(
        String transactionId,
        String accountNumber,
        BigDecimal amount,
        String currency
) {
}
