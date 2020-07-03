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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

/**
 * Attempts to find the currently opened class for a specified {@link Project} at the time of instantiation.
 */
public class CurrentlyOpenedClass {

    @Nullable
    private final VirtualFile sourceFile;
    @Nullable
    private final PsiClass sourceClass;

    @Inject
    CurrentlyOpenedClass(FileEditorManager editorManager,
                         FileDocumentManager documentManager,
                         PsiManager psiManager) {
        this.sourceFile = getCurrentlyOpenedFile(editorManager, documentManager);
        this.sourceClass = sourceFile != null ? findClassForFile(psiManager, sourceFile) : null;
    }

    /**
     * @return The {@link VirtualFile} which was open at the time this {@link CurrentlyOpenedClass} was instantiated,
     * null if no file was open.
     */
    @Nullable
    public VirtualFile getFile() {
        return sourceFile;
    }

    /**
     * @return The {@link PsiClass} source class which was open at the time this {@link CurrentlyOpenedClass} was instantiated,
     * null if no file was open or if the file which was open wasn't a {@link PsiClassOwner}.
     */
    @Nullable
    public PsiClass getSourceClass() {
        return sourceClass;
    }

    /**
     * @return true if this source class exists and contains an inner class with the specified name, false otherwise.
     */
    public boolean containsInnerClassWithName(@NotNull String innerClassName) {
        if (sourceClass == null) {
            return false;
        }
        PsiClass[] innerClasses = sourceClass.getInnerClasses();
        for (PsiClass innerClass : innerClasses) {
            if (innerClassName.equals(innerClass.getName())) {
                return true;
            }
        }
        return false;
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

    @Nullable
    private PsiClass findClassForFile(PsiManager psiManager, VirtualFile sourceFile) {
        PsiFile psiFile = psiManager.findFile(sourceFile);
        if (!(psiFile instanceof PsiClassOwner)) {
            return null;
        }
        return getPsiClass((PsiClassOwner) psiFile);
    }

    @Nullable
    private PsiClass getPsiClass(PsiClassOwner psiFile) {
        PsiClass[] classes = psiFile.getClasses();
        if (classes.length != 1) {
            return null;
        }
        return classes[0];
    }
}
