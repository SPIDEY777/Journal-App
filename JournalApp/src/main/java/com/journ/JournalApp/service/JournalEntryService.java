package com.journ.JournalApp.service;

import com.journ.JournalApp.entity.JournalEntry;
import com.journ.JournalApp.entity.User;
import com.journ.JournalApp.repository.JournalEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class JournalEntryService {

    private final JournalEntryRepository journalEntryRepository;
    private final UserService userService;

    @Autowired
    public JournalEntryService(JournalEntryRepository journalEntryRepository, UserService userService) {
        this.journalEntryRepository = journalEntryRepository;
        this.userService = userService;
    }

    @Transactional(rollbackFor = Exception.class)
    public JournalEntry saveEntry(JournalEntry journalEntry, String userName) {
        Optional<User> userOpt = userService.findByUserName(userName);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            journalEntry.setDate(LocalDateTime.now());
            JournalEntry saved = journalEntryRepository.save(journalEntry);
            user.getJournalEntries().add(saved);
            userService.saveUser(user);
            return saved;
        }
        throw new IllegalArgumentException("User with username '" + userName + "' not found.");
    }

    @Transactional(rollbackFor = Exception.class)
    public JournalEntry saveEntry(JournalEntry journalEntry) {
        journalEntry.setDate(LocalDateTime.now());
        return journalEntryRepository.save(journalEntry);
    }

    @Transactional(readOnly = true)
    public List<JournalEntry> getAll() {
        return journalEntryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<List<JournalEntry>> getAllEntriesOfUser(String userName) {
        return userService.findByUserName(userName).map(User::getJournalEntries);
    }

    @Transactional(readOnly = true)
    public Optional<JournalEntry> findByIdForUser(Long id, String userName) {
        return userService.findByUserName(userName)
                .flatMap(user -> user.getJournalEntries().stream()
                        .filter(entry -> entry.getId().equals(id))
                        .findFirst());
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long id, String userName) {
        Optional<User> userOpt = userService.findByUserName(userName);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            boolean removed = user.getJournalEntries().removeIf(x -> x.getId().equals(id));
            if (removed) {
                userService.saveUser(user);
                journalEntryRepository.deleteById(id);
                return true;
            }
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    public Optional<JournalEntry> updateEntry(Long id, JournalEntry newEntry, String userName) {
        Optional<User> userOpt = userService.findByUserName(userName);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Optional<JournalEntry> journalEntryOpt = journalEntryRepository.findById(id);
            if (journalEntryOpt.isPresent()) {
                JournalEntry existingEntry = journalEntryOpt.get();
                boolean belongsToUser = user.getJournalEntries().stream()
                        .anyMatch(x -> x.getId().equals(id));
                if (belongsToUser) {
                    if (newEntry.getTitle() != null && !newEntry.getTitle().isEmpty()) {
                        existingEntry.setTitle(newEntry.getTitle());
                    }
                    if (newEntry.getContent() != null && !newEntry.getContent().isEmpty()) {
                        existingEntry.setContent(newEntry.getContent());
                    }
                    JournalEntry saved = journalEntryRepository.save(existingEntry);
                    return Optional.of(saved);
                }
            }
        }
        return Optional.empty();
    }
}
