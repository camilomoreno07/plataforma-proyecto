package com.proyectogrado.plataforma.progress.Model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MomentProgress
{
    private Boolean instructionCompleted;
    private ContentProgress contentProgress;
    private Boolean evaluationCompleted;

    public MomentProgress(int contents)
    {
        this.instructionCompleted = false;
        this.contentProgress = new ContentProgress(contents);
        this.evaluationCompleted = false;
    }

    public double getPercentage()
    {
        double totalMoments = 3.0;
        double completedMoments = (instructionCompleted ? 1:0) + contentProgress.getPercentage() + (evaluationCompleted ? 1:0);

        return completedMoments / totalMoments;
    }
}