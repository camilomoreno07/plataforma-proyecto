package com.proyectogrado.plataforma.user;

import java.util.List;
import com.proyectogrado.plataforma.auth.Entities.User;
import com.proyectogrado.plataforma.auth.Entities.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService
{
    @Autowired
    private UserRepository userRepository;
    public List<User> findAll() {return userRepository.findAll();}
}
