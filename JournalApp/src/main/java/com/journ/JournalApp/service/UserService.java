package com.journ.JournalApp.service;

import com.journ.JournalApp.entity.User;
import com.journ.JournalApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(rollbackFor = Exception.class)
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Transactional(rollbackFor = Exception.class)
    public Optional<User> updateUser(String currentUserName, User updatedUser) {
        return userRepository.findByUserName(currentUserName).map(existingUser -> {
            if (updatedUser.getUserName() != null && !updatedUser.getUserName().isBlank()) {
                existingUser.setUserName(updatedUser.getUserName());
            }
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
                existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }
            return userRepository.save(existingUser);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByUserName(String userName) {
        Optional<User> userOpt = userRepository.findByUserName(userName);
        if (userOpt.isPresent()) {
            userRepository.deleteByUserName(userName);
            return true;
        }
        return false;
    }
}
