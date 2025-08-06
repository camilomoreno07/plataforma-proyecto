package com.proyectogrado.plataforma.service;

import com.proyectogrado.plataforma.EmbedMongoConfig;
import com.proyectogrado.plataforma.course.Model.Course;
import com.proyectogrado.plataforma.course.Service.CourseService;
import org.bson.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest
@Import(EmbedMongoConfig.class)
class CourseServiceTest
{
    @Autowired
    private CourseService courseService;

    @Test
    void findAll()
    {
        int coursesSize = courseService.findAll().size();

        Course newCourse = new Course();
        courseService.save(newCourse);

        Assertions.assertEquals(coursesSize+1, courseService.findAll().size());

        courseService.deleteById(newCourse.getCourseId());

        Assertions.assertEquals(coursesSize, courseService.findAll().size());
    }

    @Test
    void findById()
    {
        Course expectedCourse = courseService.findAll().get(0);
        Course foundCourse = courseService.findById(expectedCourse.getCourseId()).get();

        Assertions.assertEquals(expectedCourse.getCourseName(), foundCourse.getCourseName());
    }

    @Test
    void save()
    {
        Course newCourse = new Course();
        newCourse.setCourseName("Hemorragia Postparto 3");
        newCourse.setCourseDescription("Revision de conceptos H3");
        newCourse.setStudentIds(List.of("student1@gmail.com", "student2@gmail.com"));
        newCourse.setProfessorIds(List.of("teacher1@gmail.com"));

        courseService.save(newCourse);

        Assertions.assertTrue(courseService.findById(newCourse.getCourseId()).isPresent());

        courseService.deleteById(newCourse.getCourseId());
    }

    @Test
    void deleteById()
    {
        Course newCourse = new Course();
        newCourse.setCourseName("Hemorragia Postparto 4");
        newCourse.setCourseDescription("Revision de conceptos H4");
        newCourse.setStudentIds(List.of("student1@gmail.com", "student2@gmail.com"));
        newCourse.setProfessorIds(List.of("teacher1@gmail.com"));

        courseService.save(newCourse);

        int coursesBeforeDelete = courseService.findAll().size();

        courseService.deleteById(newCourse.getCourseId());

        Assertions.assertEquals(coursesBeforeDelete-1, courseService.findAll().size());
    }

    @Test
    void findStudentWithCourses()
    {
        List<Document> response = courseService
                .findStudentWithCourses("student1@gmail.com")
                .getList("results", Document.class);

        String student = null;
        Set<String> courses = null;
        if (response != null && !response.isEmpty())
        {
            student = response.get(0).get("student", String.class);
            courses = new HashSet<>(response.get(0).getList("courses", String.class));
        }

        String expectedStudent = "The First Student";
        Set<String> expectedCourses = Set.of("Hemorragia Postparto 1", "Hemorragia Postparto 2");

        Assertions.assertEquals(expectedStudent, student);
        Assertions.assertEquals(expectedCourses, courses);
    }
}