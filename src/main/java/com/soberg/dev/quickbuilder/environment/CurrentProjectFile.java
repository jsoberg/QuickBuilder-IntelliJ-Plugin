package com.soberg.dev.quickbuilder.environment;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassOwner;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.soberg.dev.quickbuilder.generation.GenerationConstants;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Attempts to find the currently opened file for a specified {@link Project} at the time of instantiation.
 */
public class CurrentProjectFile {

    @Nullable
    private final VirtualFile sourceFile;
    @Nonnull
    private final List<PsiClass> buildableClasses = new ArrayList<>();

    @Inject
    CurrentProjectFile(FileEditorManager editorManager,
                       FileDocumentManager documentManager,
                       PsiManager psiManager) {
        sourceFile = getCurrentlyOpenedFile(editorManager, documentManager);
        if (sourceFile != null) {
            addBuildableClassesForFile(buildableClasses, psiManager, sourceFile);
        }
    }

    /**
     * @return The {@link VirtualFile} which was open at the time this {@link CurrentProjectFile} was instantiated,
     * null if no file was open.
     */
    @Nullable
    public VirtualFile getFile() {
        return sourceFile;
    }

    /**
     * @return The {@link PsiClass} source classes which were open at the time this {@link CurrentProjectFile} was instantiated.
     * If no file was open or if the file that was open wasn't a {@link PsiClassOwner}, this list will be empty.
     */
    @Nonnull
    public List<PsiClass> getBuildableClasses() {
        return buildableClasses;
    }

    @Nullable
    private VirtualFile getCurrentlyOpenedFile(FileEditorManager editorManager, FileDocumentManager documentManager) {
        Editor textEditor = editorManager.getSelectedTextEditor();
        if (textEditor == null) {
            return null;
        }
        Document document = textEditor.getDocument();
        return documentManager.getFile(document);
    }

    private void addBuildableClassesForFile(List<PsiClass> buildableClasses, PsiManager psiManager, VirtualFile sourceFile) {
        PsiFile psiFile = psiManager.findFile(sourceFile);
        if (!(psiFile instanceof PsiClassOwner)) {
            return;
        }
        PsiClassOwner classOwner = (PsiClassOwner) psiFile;
        for (PsiClass sourceClass : classOwner.getClasses()) {
            addBuildableClasses(buildableClasses, sourceClass);
        }
    }

    private void addBuildableClasses(List<PsiClass> buildableClasses, PsiClass psiClass) {
        if (isClassBuildable(psiClass)) {
            buildableClasses.add(psiClass);
        }
        for (PsiClass innerClass : psiClass.getInnerClasses()) {
            addBuildableClasses(buildableClasses, innerClass);
        }
    }

    private boolean isClassBuildable(PsiClass psiClass) {
        // If this class is itself a Builder, it isn't buildable.
        if (isBuilderClass(psiClass)) {
            return false;
        }
        // If a direct inner class of this class is a Builder, this class isn't buildable.
        for (PsiClass innerClass : psiClass.getInnerClasses()) {
            if (isBuilderClass(innerClass)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return true if the provided {@link PsiClass} is a Builder class, false otherwise.
     */
    private boolean isBuilderClass(@Nonnull PsiClass psiClass) {
        return GenerationConstants.BUILDER_CLASS_NAME.equals(psiClass.getName());
    }
}
