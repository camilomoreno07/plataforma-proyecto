package com.proyectogrado.plataforma.service;

import com.proyectogrado.plataforma.EmbedMongoConfig;
import com.proyectogrado.plataforma.activity.Models.Activity;
import com.proyectogrado.plataforma.activity.Service.ActivityService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;


@SpringBootTest
@Import(EmbedMongoConfig.class)
class ActivityServiceTest
{
    @Autowired
    private ActivityService activityService;

    @Test
    void findAll()
    {
        int initialSize = activityService.findAll().size();

        Activity activity = new Activity();
        activity.setTitulo("Introducción a Java");
        activity.setResumen("Conceptos básicos de Java");

        activityService.save(activity);

        Assertions.assertEquals(initialSize + 1, activityService.findAll().size());

        activityService.deleteById(activity.getActivityId());
    }

    @Test
    void findById()
    {
        Activity activity = new Activity();
        activity.setTitulo("POO en Java");
        activity.setResumen("Clases, objetos y herencia");

        activityService.save(activity);

        Activity found = activityService.findById(activity.getActivityId()).orElse(null);
        Assertions.assertNotNull(found);
        Assertions.assertEquals("POO en Java", found.getTitulo());

        activityService.deleteById(activity.getActivityId());
    }

    @Test
    void save()
    {
        Activity activity = new Activity();
        activity.setTitulo("Colecciones en Java");
        activity.setResumen("Listas, sets y mapas");

        activityService.save(activity);

        Optional<Activity> saved = activityService.findById(activity.getActivityId());
        Assertions.assertTrue(saved.isPresent());
        Assertions.assertEquals("Colecciones en Java", saved.get().getTitulo());

        activityService.deleteById(activity.getActivityId());
    }

    @Test
    void deleteById()
    {
        Activity activity = new Activity();
        activity.setTitulo("Streams en Java");
        activity.setResumen("Procesamiento de datos con streams");

        activityService.save(activity);
        String id = activity.getActivityId();

        Assertions.assertTrue(activityService.findById(id).isPresent());

        activityService.deleteById(id);

        Assertions.assertFalse(activityService.findById(id).isPresent());
    }
}