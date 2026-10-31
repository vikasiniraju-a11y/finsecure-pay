package com.finsecure.pay;

import org.springframework.web.bind.annotation.*;

@RestController
public class TransactionController {

    private final TransactionValidationService validationService;

    public TransactionController(
            TransactionValidationService validationService) {
        this.validationService = validationService;
    }

    @PostMapping("/validate")
    public TransactionResponse validate(
            @RequestBody TransactionRequest request) {

        return validationService.validate(request);
    }
}
