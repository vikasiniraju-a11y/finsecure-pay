package com.finsecure.pay;

import java.math.BigDecimal;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class TransactionValidationService {

    private static final Set<String> SUPPORTED_CURRENCIES =
            Set.of("SGD", "USD", "EUR");

    public TransactionResponse validate(TransactionRequest request) {

        if (request == null) {
            return new TransactionResponse(
                    null, "REJECTED", "Transaction request is required");
        }

        String transactionId = request.transactionId();

        if (transactionId == null || transactionId.isBlank()) {
            return new TransactionResponse(
                    transactionId, "REJECTED",
                    "Transaction ID is required");
        }

        if (request.accountNumber() == null ||
                request.accountNumber().isBlank()) {
            return new TransactionResponse(
                    transactionId, "REJECTED",
                    "Account number is required");
        }

        BigDecimal amount = request.amount();

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {
            return new TransactionResponse(
                    transactionId, "REJECTED",
                    "Transaction amount must be greater than zero");
        }

        String currency = request.currency();

        if (currency == null ||
                !SUPPORTED_CURRENCIES.contains(currency)) {
            return new TransactionResponse(
                    transactionId, "REJECTED",
                    "Unsupported currency. Use SGD, USD or EUR");
        }

        return new TransactionResponse(
                transactionId,
                "APPROVED",
                "Transaction validated successfully - v1.1");
    }
}
