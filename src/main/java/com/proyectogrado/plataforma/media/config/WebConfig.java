package com.proyectogrado.plataforma.media.config;

import org.springframework.context.annotation.Configuration;

import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.http.MediaType;

@Configuration
public class WebConfig implements WebMvcConfigurer
{
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        String experiencesDir = System.getProperty("user.dir") + "/uploads/experiences/";
        registry.addResourceHandler("/media/experiences/**")
                .addResourceLocations("file:"+experiencesDir);
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer)
    {
        configurer
                .mediaType("css", MediaType.valueOf("text/css"))
                .mediaType("js", MediaType.valueOf("application/javascript"))
                .mediaType("wasm", MediaType.valueOf("application/wasm"));
    }
}