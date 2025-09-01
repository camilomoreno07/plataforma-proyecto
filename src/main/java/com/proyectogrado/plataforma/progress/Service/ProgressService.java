package com.proyectogrado.plataforma.progress.Service;


import com.proyectogrado.plataforma.progress.Model.Progress;
import com.proyectogrado.plataforma.progress.Repository.ProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProgressService
{

    @Autowired
    private ProgressRepository progressRepository;

    /** Save or update progress */
    public Progress saveProgress(Progress progress) {
        return progressRepository.save(progress);
    }

    /** Find by ID */
    public Optional<Progress> getById(String id) {
        return progressRepository.findById(id);
    }

    /** Find by courseId and studentId */
    public Optional<Progress> getByCourseAndStudent(String courseId, String studentId) {
        return progressRepository.findByCourseIdAndStudentId(courseId, studentId);
    }

    /** Delete by ID */
    public void deleteById(String id) {
        progressRepository.deleteById(id);
    }

    /** Check existence */
    public boolean existsById(String id) {
        return progressRepository.existsById(id);
    }

    /** Calculate percentage */
    public Optional<Double> getPercentage(String id) {
        return progressRepository.findById(id).map(Progress::getPercentage);
    }

    public Optional<Double> getPercentageByCourseAndStudent(String courseId, String studentId)
    {
        Optional<Progress> progress = progressRepository.findByCourseIdAndStudentId(courseId, studentId);
        return progress.map(Progress::getPercentage);
    }

    /** Find all progress entries by courseId */
    public List<Progress> getAllByCourseId(String courseId) {
        return progressRepository.findAllByCourseId(courseId);
    }
}