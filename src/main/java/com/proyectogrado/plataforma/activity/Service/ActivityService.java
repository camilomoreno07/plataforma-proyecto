package com.proyectogrado.plataforma.activity.Service;

import com.proyectogrado.plataforma.activity.Models.Activity;
import com.proyectogrado.plataforma.activity.Repositories.ActivityRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ActivityService {

    @Autowired
    private ActivityRepository repository;

    public List<Activity> findAll() {
        return repository.findAll();
    }

    public Optional<Activity> findById(String id) {
        return repository.findById(id);
    }

    public Activity save(Activity activity) {
        return repository.save(activity);
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
