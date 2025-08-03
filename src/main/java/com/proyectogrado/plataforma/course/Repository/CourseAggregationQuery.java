package com.proyectogrado.plataforma.course.Repository;

import org.bson.Document;

public interface CourseAggregationQuery
{
    Document findStudentWithCourses(String username);
}
