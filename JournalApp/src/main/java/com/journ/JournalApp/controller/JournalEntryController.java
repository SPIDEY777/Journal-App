package com.journ.JournalApp.controller;

import com.journ.JournalApp.entity.JournalEntry;
import com.journ.JournalApp.service.JournalEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {

    private final JournalEntryService journalEntryService;

    @Autowired
    public JournalEntryController(JournalEntryService journalEntryService) {
        this.journalEntryService = journalEntryService;
    }

    @GetMapping
    public ResponseEntity<List<JournalEntry>> getAllJournalEntriesOfCurrentUser(Authentication authentication) {
        return journalEntryService.getAllEntriesOfUser(authentication.getName())
                .map(entries -> new ResponseEntity<>(entries, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<JournalEntry> createEntry(Authentication authentication, @RequestBody JournalEntry entry) {
        try {
            JournalEntry savedEntry = journalEntryService.saveEntry(entry, authentication.getName());
            return new ResponseEntity<>(savedEntry, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<JournalEntry> getJournalById(Authentication authentication, @PathVariable Long id) {
        return journalEntryService.findByIdForUser(id, authentication.getName())
                .map(entry -> new ResponseEntity<>(entry, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> deleteById(Authentication authentication, @PathVariable Long id) {
        boolean removed = journalEntryService.deleteById(id, authentication.getName());
        if (removed) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<JournalEntry> updateById(Authentication authentication, @PathVariable Long id, @RequestBody JournalEntry newEntry) {
        return journalEntryService.updateEntry(id, newEntry, authentication.getName())
                .map(entry -> new ResponseEntity<>(entry, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
