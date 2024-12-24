package com.proyectogrado.plataforma.course.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

import java.util.List;

@Data
@Document(collection = "courses")
public class Course {

    @Id
    private String courseId;
    private String courseName;
    private List<String> professorIds;
    private List<String> studentIds;
    private List<String> activityIds;

}
