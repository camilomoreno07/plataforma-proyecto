package com.proyectogrado.plataforma.course.Service;

import com.proyectogrado.plataforma.course.Model.Course;
import com.proyectogrado.plataforma.course.Repository.CourseRepository;
import com.proyectogrado.plataforma.progress.Model.Progress;
import com.proyectogrado.plataforma.progress.Repository.ProgressRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.bson.Document;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ProgressRepository studentCourseProgressRepository;

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Optional<Course> findById(String id) {
        return courseRepository.findById(id);
    }

    public Course save(Course course) {
        Course savedCourse = courseRepository.save(course);

        // Crear progreso para cada estudiante si no existe aún
        if (savedCourse.getStudentIds() != null) {
            for (String studentEmail : savedCourse.getStudentIds()) {
                boolean progressAlreadyExists = studentCourseProgressRepository
                        .existsByCourseIdAndStudentEmail(savedCourse.getCourseId(), studentEmail);

                if (!progressAlreadyExists) {
                    Progress progress = new Progress();
                    progress.setCourseId(savedCourse.getCourseId());
                    progress.setStudentEmail(studentEmail);
                    progress.setProgressStatus("pendiente");
                    progress.setCompletedEvaluations(new java.util.ArrayList<>());
                    progress.setCurrentStep(null);
                    progress.setStartedAt(LocalDateTime.now());
                    progress.setCompletedAt(null);

                    studentCourseProgressRepository.save(progress);
                }
            }
        }

        return savedCourse;
    }

    public void deleteById(String id) {
        Optional<Course> optionalCourse = courseRepository.findById(id);

        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();

            // 1. Recolectar todas las imágenes asociadas
            List<String> imageUrls = new java.util.ArrayList<>();

            if (course.getImageUrl() != null) {
                imageUrls.add(course.getImageUrl());
            }

            if (course.getBeforeClass() != null && course.getBeforeClass().getContents() != null) {
                course.getBeforeClass().getContents().forEach(content -> {
                    if (content.getImageUrl() != null) {
                        imageUrls.add(content.getImageUrl());
                    }
                });
            }

            if (course.getDuringClass() != null && course.getDuringClass().getContents() != null) {
                course.getDuringClass().getContents().forEach(content -> {
                    if (content.getImageUrl() != null) {
                        imageUrls.add(content.getImageUrl());
                    }
                });
            }

            if (course.getAfterClass() != null && course.getAfterClass().getContents() != null) {
                course.getAfterClass().getContents().forEach(content -> {
                    if (content.getImageUrl() != null) {
                        imageUrls.add(content.getImageUrl());
                    }
                });
            }

            // 2. Eliminar los archivos físicos de imágenes
            imageUrls.forEach(url -> {
                if (url.contains("/media/files/")) {
                    String filename = url.substring(url.indexOf("/media/files/") + "/media/files/".length());
                    try {
                        Path filePath = Paths.get(System.getProperty("user.dir"), "uploads", filename);
                        File file = filePath.toFile();
                        if (file.exists()) {
                            file.delete(); // Elimina el archivo localmente
                        }
                    } catch (Exception e) {
                        System.out.println("Error eliminando imagen: " + filename);
                    }
                }
            });

            // 3. Eliminar progresos de estudiantes
            studentCourseProgressRepository.deleteByCourseId(course.getCourseId());

            // 4. Finalmente eliminar el curso
            courseRepository.deleteById(id);
        }
    }

    public Document findStudentWithCourses(String username)
    {
        return courseRepository.findStudentWithCourses(username);
    }
}
