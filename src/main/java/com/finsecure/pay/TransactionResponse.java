package com.finsecure.pay;

public record TransactionResponse(
        String transactionId,
        String status,
        String message
) {
}
