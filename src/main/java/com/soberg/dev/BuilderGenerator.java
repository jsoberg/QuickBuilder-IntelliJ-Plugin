package com.soberg.dev;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassOwner;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;

public class BuilderGenerator {
    private final Project project;

    public BuilderGenerator(Project project) {
        this.project = project;
    }

    public void generateBuilder(VirtualFile sourceFile) throws BuilderGenerationException {
        PsiClass fileClass = findClassForFile(sourceFile);
    }

    public PsiClass findClassForFile(VirtualFile sourceFile) throws BuilderGenerationException {
        PsiFile psiFile = PsiManager.getInstance(project).findFile(sourceFile);
        if (!(psiFile instanceof PsiClassOwner)) {
            throw new BuilderGenerationException(sourceFile.getName() + " does not contain a class");
        }
        PsiClass[] classes = ((PsiClassOwner) psiFile).getClasses();
        if (classes.length != 1) {
            throw new BuilderGenerationException(sourceFile.getName() + " contains more than one class");
        }
        return classes[0];
    }

}
