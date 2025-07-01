package com.proyectogrado.plataforma.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import com.proyectogrado.plataforma.auth.Entities.User;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
