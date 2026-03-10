package com.example.myportfolio.service;

import com.example.myportfolio.entity.Contact;
import com.example.myportfolio.repository.ContactRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactService {

    private final ContactRepository contactRepository;
    private final JavaMailSender mailSender;

    public ContactService(ContactRepository contactRepository, JavaMailSender mailSender) {
        this.contactRepository = contactRepository;
        this.mailSender = mailSender;
    }

    public Contact saveMessage(Contact contact) {
        Contact savedContact = contactRepository.save(contact);
        sendEmailNotification(savedContact);
        return savedContact;
    }

    public List<Contact> getAllMessages() {
        return contactRepository.findAll();
    }

    private void sendEmailNotification(Contact contact) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("syedharoon.360@gmail.com"); // Your email where you want to receive it
            message.setSubject("New Portfolio Message from " + contact.getName());
            message.setText("You have received a new message from your portfolio website!\n\n" +
                    "Name: " + contact.getName() + "\n" +
                    "Email: " + contact.getEmail() + "\n\n" +
                    "Message:\n" + contact.getMessage());

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send email notification: " + e.getMessage());
            // We catch the exception so that if the email fails, the message is still saved
            // in the DB
            // and the frontend still gets a "success" response.
        }
    }
}
