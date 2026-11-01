
package com.finsecure.pay;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransactionValidationServiceTest {

    private TransactionValidationService service;

    @BeforeEach
    void setUp() {
        service = new TransactionValidationService();
    }

    @Test
    void validTransactionShouldBeApproved() {
        var request = new TransactionRequest(
            "TXN1001", "ACC1001",
            new BigDecimal("500"), "SGD");

        assertEquals("REJECTED",
            service.validate(request).status());
    }

    @Test
    void negativeAmountShouldBeRejected() {
        var request = new TransactionRequest(
            "TXN1002", "ACC1001",
            new BigDecimal("-100"), "SGD");

        assertEquals("REJECTED",
            service.validate(request).status());
    }

    @Test
    void zeroAmountShouldBeRejected() {
        var request = new TransactionRequest(
            "TXN1003", "ACC1001",
            BigDecimal.ZERO, "USD");

        assertEquals("REJECTED",
            service.validate(request).status());
    }

    @Test
    void unsupportedCurrencyShouldBeRejected() {
        var request = new TransactionRequest(
            "TXN1004", "ACC1001",
            new BigDecimal("250"), "XYZ");

        assertEquals("REJECTED",
            service.validate(request).status());
    }

    @Test
    void missingTransactionIdShouldBeRejected() {
        var request = new TransactionRequest(
            "", "ACC1001",
            new BigDecimal("100"), "SGD");

        assertEquals("REJECTED",
            service.validate(request).status());
    }

    @Test
    void missingAccountShouldBeRejected() {
        var request = new TransactionRequest(
            "TXN1006", "",
            new BigDecimal("100"), "SGD");

        assertEquals("REJECTED",
            service.validate(request).status());
    }

    @Test
    void missingAmountShouldBeRejected() {
        var request = new TransactionRequest(
            "TXN1007", "ACC1001",
            null, "SGD");

        assertEquals("REJECTED",
            service.validate(request).status());
    }

    @Test
    void validEuroTransactionShouldBeApproved() {
        var request = new TransactionRequest(
            "TXN1008", "ACC1001",
            new BigDecimal("250"), "EUR");

        assertEquals("APPROVED",
            service.validate(request).status());
    }
}

