package com.proyectogrado.plataforma.course.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "contents")
public class Content {

    @Id
    private String contentId;
    private String contentTitle;
    private String contentDescription;
    private Integer time;

}
