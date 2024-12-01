package com.springsecurity.controller;

import com.springsecurity.model.Cards;
import com.springsecurity.repository.CardsRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CardsController {

  private final CardsRepository cardsRepository;

  @GetMapping("/myCards")
  public List<Cards> getCardDetails(@RequestParam long id) {
    List<Cards> cards = cardsRepository.findByCustomerId(id);
    if (cards != null ) {
      return cards;
    }else {
      return null;
    }
  }

}
