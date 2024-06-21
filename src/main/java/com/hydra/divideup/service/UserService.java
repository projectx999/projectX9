package com.hydra.divideup.service;

import com.hydra.divideup.entity.User;
import com.hydra.divideup.exception.RecordAlreadyExistsException;
import com.hydra.divideup.exception.RecordNotFoundException;
import com.hydra.divideup.model.UserDTO;
import com.hydra.divideup.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

import static com.hydra.divideup.exception.DivideUpError.*;

@Service
public class UserService {


    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;
    private final Supplier<RecordNotFoundException> userNotFoundSupplier =
            () -> new RecordNotFoundException(USER_NOT_FOUND);

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(UserDTO userDTO) {
        String encodedPwd = passwordEncoder.encode(userDTO.getPassword());
        User newUser = new User(userDTO.getEmail(), userDTO.getPhone(), encodedPwd);
        validateCreateUser(newUser);
        return userRepository.save(newUser);
    }

    public User getUser(String id) {
        return userRepository.findById(id)
                .orElseThrow(userNotFoundSupplier);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User updateUser(String id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(USER_NOT_FOUND));
        validateUpdateUser(userDTO, existingUser);
        existingUser.setName(userDTO.getName());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setPhoneNumber(userDTO.getPhone());
        existingUser.setCountry(userDTO.getCountry());
        existingUser.setDefaultCurrency(userDTO.getDefaultCurrency());
        existingUser.setLanguage(userDTO.getLanguage());
        return userRepository.save(existingUser);
    }

    void validateUpdateUser(UserDTO userDTO, User existingUser) {
        List<User> byEmailOrPhoneNumber = userRepository.findByEmailOrPhoneNumber(userDTO.getEmail(),
                userDTO.getPhone());
        if (!existingUser.getEmail().equalsIgnoreCase(userDTO.getEmail())) {
            byEmailOrPhoneNumber.stream().map(User::getEmail)
                    .filter(s -> s.equalsIgnoreCase(userDTO.getEmail()))
                    .findAny().ifPresent(u -> {
                        throw new RecordAlreadyExistsException(USER_EMAIL_EXISTS);
                    });
        }
        if (!existingUser.getPhoneNumber().equalsIgnoreCase(userDTO.getPhone())) {
            byEmailOrPhoneNumber.stream().map(User::getPhoneNumber)
                    .filter(s -> s.equalsIgnoreCase(userDTO.getPhone()))
                    .findAny().ifPresent(u -> {
                        throw new RecordAlreadyExistsException(USER_PHONE_EXISTS);
                    });
        }
    }

    void validateCreateUser(User user) {
        List<User> byEmailOrPhoneNumber = userRepository.findByEmailOrPhoneNumber(user.getEmail(),
                user.getPhoneNumber());

        byEmailOrPhoneNumber.stream().map(User::getEmail)
                .filter(s -> s.equalsIgnoreCase(user.getEmail()))
                .findAny().ifPresent(u -> {
                    throw new RecordAlreadyExistsException(USER_EMAIL_EXISTS);
                });

        byEmailOrPhoneNumber.stream().map(User::getPhoneNumber)
                .filter(s -> s.equalsIgnoreCase(user.getPhoneNumber()))
                .findAny().ifPresent(u -> {
                    throw new RecordAlreadyExistsException(USER_PHONE_EXISTS);
                });

    }

    public User blockUser(String id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(userNotFoundSupplier);
        existingUser.setBlocked(true);
        return userRepository.save(existingUser);
    }

    public User unblockUser(String id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(userNotFoundSupplier);
        existingUser.setBlocked(false);
        return userRepository.save(existingUser);
    }

    public User deleteUser(String id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(userNotFoundSupplier);
        // todo check if user is part of any group or other validations before deleting
        existingUser.setDeleted(true);
        return userRepository.save(existingUser);
    }
}
