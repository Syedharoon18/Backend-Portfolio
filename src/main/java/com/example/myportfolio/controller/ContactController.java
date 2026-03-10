package com.example.myportfolio.controller;

import com.example.myportfolio.entity.Contact;
import com.example.myportfolio.service.ContactService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = "*")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public ResponseEntity<Contact> submitMessage(@RequestBody Contact contact) {
        return ResponseEntity.ok(contactService.saveMessage(contact));
    }

    @GetMapping
    public ResponseEntity<List<Contact>> getAllMessages() {
        return ResponseEntity.ok(contactService.getAllMessages());
    }
}
