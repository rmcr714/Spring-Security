package com.springsecurity.controller;

import com.springsecurity.model.AccountTransactions;
import com.springsecurity.repository.AccountTransactionsRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BalanceController {

  private final AccountTransactionsRepository accountTransactionsRepository;

  @GetMapping("/myBalance")
  public List<AccountTransactions> getBalanceDetails(@RequestParam long id) {
    List<AccountTransactions> accountTransactions = accountTransactionsRepository.
        findByCustomerIdOrderByTransactionDtDesc(id);
    if (accountTransactions != null) {
      return accountTransactions;
    } else {
      return null;
    }
  }
}
