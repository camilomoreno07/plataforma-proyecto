package com.proyectogrado.plataforma.course.Service;

import com.proyectogrado.plataforma.course.Model.*;
import com.proyectogrado.plataforma.course.Repository.CourseRepository;
import com.proyectogrado.plataforma.grade.Repository.GradeRepository;
import com.proyectogrado.plataforma.progress.Model.MomentProgress;
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
import java.util.function.Function;
import java.util.function.Supplier;

@Service
@AllArgsConstructor
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ProgressRepository studentCourseProgressRepository;

    @Autowired
    private GradeRepository studentGradeRepository;

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
            for (String studentId : savedCourse.getStudentIds())
            {
                Progress progress = studentCourseProgressRepository.findByCourseIdAndStudentId(savedCourse.getCourseId(), studentId)
                        .orElse(new Progress());

                progress.setCourseId(savedCourse.getCourseId());
                progress.setStudentId(studentId);

                Function<Function<Course, ClassMoment>, Integer> buildMomentProgress = classMomentMapper ->
                        Optional.of(savedCourse).map(classMomentMapper).map(ClassMoment::getContents).map(List::size).orElse(0);

                // For NA Contents or Evaluations
                Function<Function<Course, ClassMoment>, Integer> calculateOmittedMoments = classMomentMapper -> {
                    int omittedMoments = 0;

                    List<Content> contents = Optional.of(course).map(classMomentMapper).map(ClassMoment::getContents).orElse(null);
                    if(contents != null && contents.size() == 1 && (contents.get(0).getContentTitle().equals("NA") || contents.get(0).getContentTitle().equals("Sin Experiencia")))
                    {
                        omittedMoments++;
                    }

                    List<Evaluation> evaluations = Optional.of(course).map(classMomentMapper).map(ClassMoment::getEvaluations).orElse(null);
                    if(evaluations != null && evaluations.size() == 1 && evaluations.get(0).getQuestion().equals("NA"))
                    {
                        omittedMoments++;
                    }

                    return omittedMoments;
                };

                progress.setAulaInvertida(new MomentProgress(buildMomentProgress.apply(Course::getBeforeClass), calculateOmittedMoments.apply(Course::getBeforeClass)));
                progress.setTallerHabilidad(new MomentProgress(buildMomentProgress.apply(Course::getDuringClass), calculateOmittedMoments.apply(Course::getDuringClass)));
                progress.setActividadExperiencial(new MomentProgress(buildMomentProgress.apply(Course::getAfterClass), calculateOmittedMoments.apply(Course::getAfterClass)));

                studentGradeRepository.deleteByCourseId(course.getCourseId());
                studentCourseProgressRepository.save(progress);
            }
        }

        return savedCourse;
    }

    public Course reuseCourse(String sourceId, String targetId, ReuseRequest.Reuse reuse) {
        Course source = courseRepository.findById(sourceId)
                .orElseThrow(() -> new RuntimeException("Curso fuente no encontrado"));

        Course target = courseRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Curso destino no encontrado"));

        // Antes de clase
        if (reuse.getBeforeClass() != null) {
            if (reuse.getBeforeClass().isInstructions() && source.getBeforeClass() != null) {
                target.getBeforeClass().setInstructions(source.getBeforeClass().getInstructions());
            }
            if (reuse.getBeforeClass().isContents() && source.getBeforeClass() != null) {
                target.getBeforeClass().setContents(source.getBeforeClass().getContents());
            }
            if (reuse.getBeforeClass().isEvaluations() && source.getBeforeClass() != null) {
                target.getBeforeClass().setEvaluations(source.getBeforeClass().getEvaluations());
            }
        }

        // Durante la clase
        if (reuse.getDuringClass() != null) {
            if (reuse.getDuringClass().isInstructions() && source.getDuringClass() != null) {
                target.getDuringClass().setInstructions(source.getDuringClass().getInstructions());
            }
            if (reuse.getDuringClass().isContents() && source.getDuringClass() != null) {
                target.getDuringClass().setContents(source.getDuringClass().getContents());
            }
            if (reuse.getDuringClass().isEvaluations() && source.getDuringClass() != null) {
                target.getDuringClass().setEvaluations(source.getDuringClass().getEvaluations());
            }
        }

        // Después de clase
        if (reuse.getAfterClass() != null) {
            if (reuse.getAfterClass().isInstructions() && source.getAfterClass() != null) {
                target.getAfterClass().setInstructions(source.getAfterClass().getInstructions());
            }
            if (reuse.getAfterClass().isContents() && source.getAfterClass() != null) {
                target.getAfterClass().setContents(source.getAfterClass().getContents());
            }
            if (reuse.getAfterClass().isEvaluations() && source.getAfterClass() != null) {
                target.getAfterClass().setEvaluations(source.getAfterClass().getEvaluations());
            }
        }

        return save(target);
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

            // 4. Eliminar notas de estudiantes
            studentGradeRepository.deleteByCourseId(course.getCourseId());

            // 5. Finalmente eliminar el curso
            courseRepository.deleteById(id);
        }
    }

    public Document findStudentWithCourses(String username)
    {
        return courseRepository.findStudentWithCourses(username);
    }
}
