package com.proyectogrado.plataforma.media.controllers;

import com.proyectogrado.plataforma.media.services.MediaFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/media")
public class MediaFileController
{
    @Autowired
    private MediaFileService mediaFileService;

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file)
    {
        return mediaFileService.saveFile(file);
    }
}
