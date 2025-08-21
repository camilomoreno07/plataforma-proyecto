package com.proyectogrado.plataforma;

import com.proyectogrado.plataforma.auth.Auth.AuthService;
import com.proyectogrado.plataforma.auth.Auth.RegisterRequest;
import com.proyectogrado.plataforma.auth.Entities.Role;
import com.proyectogrado.plataforma.course.Model.ClassMoment;
import com.proyectogrado.plataforma.course.Model.Course;
import com.proyectogrado.plataforma.course.Model.Instruction;
import com.proyectogrado.plataforma.course.Service.CourseService;
import com.proyectogrado.plataforma.media.services.MediaFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;

@Component
@ActiveProfiles("test")
public class TestDataLoader implements CommandLineRunner
{
    @Autowired
    private AuthService authService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private MediaFileService mediaFileService;

    @Override
    public void run(String... args) throws Exception
    {
        initUsers();
        initCourses();
        initMediaFiles();
    }

    private void initUsers()
    {
        RegisterRequest studentRegisterRequest = new RegisterRequest(
                "student1@gmail.com", "1234",
                "The First", "Student",
                "Colombia", Role.STUDENT
        );

        RegisterRequest teacherRegisterRequest = new RegisterRequest(
                "teacher1@gmail.com", "1234",
                "The First", "Teacher",
                "Colombia", Role.TEACHER
        );

        RegisterRequest adminRegisterRequest = new RegisterRequest(
                "admin1@gmail.com", "1234",
                "The First", "Admin",
                "Colombia", Role.ADMIN
        );

        authService.register(studentRegisterRequest);
        authService.register(teacherRegisterRequest);
        authService.register(adminRegisterRequest);

        studentRegisterRequest = new RegisterRequest(
                "student2@gmail.com", "1234",
                "The Second", "Student",
                "Colombia", Role.STUDENT
        );

        teacherRegisterRequest = new RegisterRequest(
                "teacher2@gmail.com", "1234",
                "The Second", "Teacher",
                "Colombia", Role.TEACHER
        );

        adminRegisterRequest = new RegisterRequest(
                "admin2@gmail.com", "1234",
                "The Second", "Admin",
                "Colombia", Role.ADMIN
        );

        authService.register(studentRegisterRequest);
        authService.register(teacherRegisterRequest);
        authService.register(adminRegisterRequest);
    }

    private void initCourses()
    {
        Course course1 = new Course();
        course1.setCourseName("Hemorragia Postparto 1");
        course1.setCourseDescription("Revision de conceptos H1");
        course1.setStudentIds(List.of("student1@gmail.com", "student2@gmail.com"));
        course1.setProfessorIds(List.of("teacher1@gmail.com"));

        Instruction instruction = new Instruction();
        instruction.setInstructionTitle("Analisis de situacion");
        instruction.setInstructionDescription("Analiza una situacion real");
        instruction.setTime(3);
        instruction.setSteps(List.of());

        ClassMoment beforeClass = new ClassMoment();
        beforeClass.setInstructions(instruction);
        beforeClass.setContents(List.of());
        beforeClass.setEvaluations(List.of());

        ClassMoment duringClass = new ClassMoment();
        duringClass.setInstructions(instruction);
        duringClass.setContents(List.of());
        duringClass.setEvaluations(List.of());

        ClassMoment afterClass = new ClassMoment();
        afterClass.setInstructions(instruction);
        afterClass.setContents(List.of());
        afterClass.setEvaluations(List.of());

        course1.setBeforeClass(beforeClass);
        course1.setDuringClass(duringClass);
        course1.setAfterClass(afterClass);

        Course course2 = new Course();
        course2.setCourseName("Hemorragia Postparto 2");
        course2.setCourseDescription("Revision de conceptos H2");
        course2.setStudentIds(List.of("student1@gmail.com"));
        course2.setProfessorIds(List.of("teacher2@gmail.com"));

        course2.setBeforeClass(beforeClass);
        course2.setDuringClass(duringClass);
        course2.setAfterClass(afterClass);

        courseService.save(course1);
        courseService.save(course2);
    }

    private void initMediaFiles() throws IOException
    {
        MultipartFile file = localFileToMultipartFile(
                "example-image.jpg",
                "example-image-1.jpg",
                "image/jpeg"
        );
        mediaFileService.uploadFile(file);
    }

    private MultipartFile localFileToMultipartFile(String localFileName, String multipartFileName, String contentType)
    {
        File localFile = Paths.get("src/main/resources/test-data", localFileName).toFile();

        MultipartFile file;
        try (InputStream input = new FileInputStream(localFile))
        {
            file = new MockMultipartFile(
                    "file",
                    multipartFileName,
                    contentType,
                    input
            );
            return file;
        }
        catch (Exception e) {return null;}
    }
}
