package com.proyectogrado.plataforma.course.Controller;

import com.proyectogrado.plataforma.course.Model.Course;
import com.proyectogrado.plataforma.course.Service.CourseService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService service;

    @GetMapping
    public List<Course> getAllCourses() {
        String currentUsername = getCurrentUsername();
        List<String> currentRoles = getCurrentUserRoles();

        // Si es ADMIN, devuelve todos los cursos
        if (currentRoles.contains("ADMIN")) {
            return service.findAll();
        }

        return service.findAll()
                .stream()
                .filter(course ->
                        (course.getStudentIds() != null && course.getStudentIds().contains(currentUsername)) ||
                                (course.getProfessorIds() != null && course.getProfessorIds().contains(currentUsername))
                )
                .toList();
    }


    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable String id) {
        return service.findById(id)
                .map(course -> {
                    String currentUsername = getCurrentUsername();
                    List<String> currentRoles = getCurrentUserRoles();

                    boolean isStudentEnrolled = course.getStudentIds() != null && course.getStudentIds().contains(currentUsername);
                    boolean isProfessorAssigned = course.getProfessorIds() != null && course.getProfessorIds().contains(currentUsername);
                    boolean isAdmin = currentRoles.contains("ADMIN");

                    if (!(isStudentEnrolled || isProfessorAssigned || isAdmin)) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes acceso a este curso");
                    }

                    return ResponseEntity.ok(course);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    public Course createCourse(@RequestBody Course course) {
        return service.save(course);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable String id, @RequestBody Course course) {
        return service.findById(id)
                .map(existingCourse -> {
                    course.setCourseId(id);
                    return ResponseEntity.ok(service.save(course));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable String id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/forStudent/{studentUsername}")
    public ResponseEntity<Map<String, Object>> getCoursesAssociatedToStudent(@PathVariable String studentUsername)
    {
        String currentUsername = getCurrentUsername();
        List<String> currentRoles = getCurrentUserRoles();

        boolean isAdmin = currentRoles.contains("ADMIN");
        boolean isAuthorizedStudent = currentRoles.contains("STUDENT") && currentUsername.equals(studentUsername);

        if (!(isAdmin || isAuthorizedStudent))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para acceder a esta informacion");
        }

        Document document = service.findStudentWithCourses(studentUsername);

        List<Document> results = document.getList("results", Document.class);
        if(results.isEmpty()){return ResponseEntity.notFound().build();}

        Document result = results.get(0);
        Map<String, Object> response = Map.of(
                "student", result.get("student"),
                "courses", result.get("courses")
        );

        return ResponseEntity.ok(response);
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
