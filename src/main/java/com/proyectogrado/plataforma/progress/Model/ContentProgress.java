package com.proyectogrado.plataforma.progress.Model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ContentProgress
{
    private List<Boolean> contentsCompleted;

    public ContentProgress(int contents)
    {
        this.contentsCompleted = new ArrayList<>(contents);
        for (int i = 0; i < contents; i++)
        {
            contentsCompleted.add(false);
        }
    }

    public double getPercentage()
    {
        int totalContents = contentsCompleted.size();

        long completedContents = contentsCompleted.stream()
                .filter(Boolean::booleanValue)
                .count();

        return ((double) completedContents) / totalContents;
    }
}