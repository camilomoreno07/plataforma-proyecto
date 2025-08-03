package com.proyectogrado.plataforma.media.exceptions;

public class InvalidExperienceException extends RuntimeException
{
    public InvalidExperienceException()
    {
        super("The experience doesn't follow the correspondent format:\n - Build\n - TemplateData\n - index.html");
    }
}
