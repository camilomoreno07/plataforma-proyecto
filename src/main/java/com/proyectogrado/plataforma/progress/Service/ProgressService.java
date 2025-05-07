package com.proyectogrado.plataforma.progress.Service;


import com.proyectogrado.plataforma.progress.Model.Progress;
import com.proyectogrado.plataforma.progress.Repository.ProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProgressService {

    @Autowired
    private ProgressRepository repository;

    // Guardar o actualizar progreso
    public Progress saveProgress(Progress progress) {
        return repository.save(progress);
    }

    // Buscar progreso de un estudiante en un curso
    public Optional<Progress> findByCourseIdAndStudentEmail(String courseId, String studentEmail) {
        return repository.findByCourseIdAndStudentEmail(courseId, studentEmail);
    }

    // Traer todos los progresos de un curso (ej: para que el profe vea)
    public List<Progress> findAllByCourseId(String courseId) {
        return repository.findAllByCourseId(courseId);
    }

    // Borrar progreso de un estudiante
    public void deleteProgress(String id) {
        repository.deleteById(id);
    }

}
