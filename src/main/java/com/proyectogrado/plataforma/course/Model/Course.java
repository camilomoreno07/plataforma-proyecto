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
    private String courseDescription;
    private List<String> professorIds;
    private List<String> studentIds;
    private ClassMoment beforeClass;
    private ClassMoment duringClass;
    private ClassMoment afterClass;
    private Boolean isPublic;
    private String imageUrl;
    private MomentStatus momentStatus;
    private String teacherName;
    private String teacherTitle;
    private String teacherEmail;
}
