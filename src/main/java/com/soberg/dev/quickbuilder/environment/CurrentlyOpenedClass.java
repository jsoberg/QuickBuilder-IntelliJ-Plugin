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
import org.jetbrains.annotations.Nullable;

public class CurrentlyOpenedClass {

    @Nullable
    private final VirtualFile sourceFile;
    @Nullable
    private final PsiClass sourceClass;

    public CurrentlyOpenedClass(Project project) {
        this.sourceFile = getCurrentlyOpenedFile(project);
        this.sourceClass = sourceFile != null ? findClassForFile(project, sourceFile) : null;
    }

    @Nullable
    public VirtualFile getSourceFile() {
        return sourceFile;
    }

    @Nullable
    public PsiClass getSourceClass() {
        return sourceClass;
    }

    @Nullable
    private VirtualFile getCurrentlyOpenedFile(@Nullable Project project) {
        Editor textEditor = (project != null) ? FileEditorManager.getInstance(project).getSelectedTextEditor() : null;
        if (textEditor == null) {
            return null;
        }
        Document document = textEditor.getDocument();
        return FileDocumentManager.getInstance().getFile(document);
    }

    @Nullable
    private PsiClass findClassForFile(Project project, VirtualFile sourceFile) {
        PsiFile psiFile = PsiManager.getInstance(project).findFile(sourceFile);
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

    public boolean isValidClass() {
        return sourceClass != null;
    }
}
