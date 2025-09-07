package com.proyectogrado.plataforma.user;

import java.util.List;
import java.util.Optional;

import com.proyectogrado.plataforma.auth.Entities.User;
import com.proyectogrado.plataforma.auth.Entities.UserRepository;

import com.proyectogrado.plataforma.auth.ExceptionHandling.UsernameNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService
{
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    public List<User> findAll() {return userRepository.findAll();}
    public Optional<User> findByUsername(String username) {return userRepository.findByUsername(username);}

    public void deleteByUsername(String username) {
        userRepository.deleteByUsername(username);
    }

    public User updateUser(String username, User updatedUser)
    {
        Optional<User> existingUserOpt = userRepository.findByUsername(username);
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            // Update only non-null fields
            if (updatedUser.getFirstname() != null) {
                existingUser.setFirstname(updatedUser.getFirstname());
            }
            if (updatedUser.getLastname() != null) {
                existingUser.setLastname(updatedUser.getLastname());
            }
            if (updatedUser.getUsername() != null) {
                existingUser.setUsername(updatedUser.getUsername());
            }
            if (updatedUser.getRole() != null) {
                existingUser.setRole(updatedUser.getRole());
            }
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }
            // Save the updated user
            return userRepository.save(existingUser);
        } else {
            throw new UsernameNotFoundException("User with username " + username + " not found.");
        }
    }
}
