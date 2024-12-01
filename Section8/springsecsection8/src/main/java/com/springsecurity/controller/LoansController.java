package com.springsecurity.controller;

import com.springsecurity.model.Loans;
import com.springsecurity.repository.LoanRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoansController {

  private final LoanRepository loanRepository;

  @GetMapping("/myLoans")
  public List<Loans> getLoanDetails(@RequestParam long id) {
    List<Loans> loans = loanRepository.findByCustomerIdOrderByStartDtDesc(id);
    if (loans != null) {
      return loans;
    } else {
      return null;
    }
  }

}
