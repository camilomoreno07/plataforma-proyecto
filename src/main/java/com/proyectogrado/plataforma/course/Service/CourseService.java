package com.proyectogrado.plataforma.course.Service;

import com.proyectogrado.plataforma.course.Model.Course;
import com.proyectogrado.plataforma.course.Repository.CourseRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CourseService {

    @Autowired
    private CourseRepository repository;

    public List<Course> findAll() {
        return repository.findAll();
    }

    public Optional<Course> findById(String id) {
        return repository.findById(id);
    }

    public Course save(Course course) {
        return repository.save(course);
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }

}
