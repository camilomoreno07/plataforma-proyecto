package com.proyectogrado.plataforma.user;

import com.proyectogrado.plataforma.auth.Entities.User;
import com.proyectogrado.plataforma.auth.ExceptionHandling.UsernameNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController
{
    @Autowired
    private UserService service;

    @GetMapping
    public List<User> getAllUsers()
    {
        List<String> currentRoles = getCurrentUserRoles();

        // Si es ADMIN, devuelve todos los users
        if (currentRoles.contains("ADMIN"))
        {
            return service.findAll();
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para acceder a esta informacion");
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username)
    {
        return service.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete a user by username
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username)
    {
        List<String> currentRoles = getCurrentUserRoles();
        if (currentRoles.contains("ADMIN")) {
            service.deleteByUsername(username);
            return ResponseEntity.noContent().build();
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para eliminar usuarios.");
        }
    }

    // Update a user by username
    @PutMapping("/{username}")
    public ResponseEntity<User> updateUser(@PathVariable String username, @RequestBody User updatedUser)
    {
        List<String> currentRoles = getCurrentUserRoles();
        if (currentRoles.contains("ADMIN")) {
            try {
                User updated = service.updateUser(username, updatedUser);
                return ResponseEntity.ok(updated);
            } catch (UsernameNotFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // Return 404 if user is not found
            }
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para actualizar usuarios.");
        }
    }

    // Función para obtener el usuario autenticado
    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    private List<String> getCurrentUserRoles() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getAuthorities().stream()
                    .map(authority -> authority.getAuthority())
                    .toList();
        }
        return List.of();
    }
}