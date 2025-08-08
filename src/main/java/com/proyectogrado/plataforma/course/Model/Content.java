package com.proyectogrado.plataforma.course.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
public class Content {

    private String contentTitle;
    private String contentDescription;
    private Integer time;
    private String imageUrl;
    private String fileType;
    private String experienceUrl;

}
