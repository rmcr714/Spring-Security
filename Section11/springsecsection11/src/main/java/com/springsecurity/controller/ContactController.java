package com.springsecurity.controller;

import com.springsecurity.model.Contact;
import com.springsecurity.repository.ContactRepository;
import java.util.Date;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ContactController {

  private final ContactRepository contactRepository;

  @PostMapping("/contact")
  public Contact saveContactInquiryDetails(@RequestBody Contact contact) {
    contact.setContactId(getServiceReqNumber());
    contact.setCreateDt(new Date(System.currentTimeMillis()));
    return contactRepository.save(contact);
  }

  public String getServiceReqNumber() {
    Random random = new Random();
    int ranNum = random.nextInt(999999999 - 9999) + 9999;
    return "SR" + ranNum;
  }
}
