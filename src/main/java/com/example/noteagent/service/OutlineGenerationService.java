package com.example.noteagent.service;

import com.example.noteagent.agent.MaterialAnalysisAgent;
import com.example.noteagent.agent.MergePlannerAgent;
import com.example.noteagent.agent.OutlineAgent;
import com.example.noteagent.model.MaterialAnalysis;
import com.example.noteagent.model.MaterialDocument;
import com.example.noteagent.model.MergePlanningResult;
import com.example.noteagent.model.NotePreferences;
import com.example.noteagent.model.OutlineGenerationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OutlineGenerationService {

    private final MaterialAnalysisAgent materialAnalysisAgent;
    private final OutlineAgent outlineAgent;
    private final MergePlannerAgent mergePlannerAgent;

    public MaterialAnalysis analyzeMaterials(List<MaterialDocument> materials, NotePreferences preferences) {
        return materialAnalysisAgent.analyze(materials, preferences);
    }

    public OutlineGenerationResult generateOrganizeOutline(MaterialAnalysis analysis,
                                                           List<MaterialDocument> materials,
                                                           NotePreferences preferences) {
        return outlineAgent.generate(analysis, materials, preferences);
    }

    public MergePlanningResult generateIncrementalPlan(MaterialDocument existingNote,
                                                       List<MaterialDocument> newMaterials,
                                                       MaterialAnalysis newMaterialAnalysis,
                                                       NotePreferences preferences) {
        return mergePlannerAgent.plan(existingNote, newMaterials, newMaterialAnalysis, preferences);
    }
}
