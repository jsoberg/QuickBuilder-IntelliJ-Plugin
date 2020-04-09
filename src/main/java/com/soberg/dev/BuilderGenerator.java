package com.soberg.dev;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;

public class BuilderGenerator {

    private final Project project;

    public BuilderGenerator(Project project) {
        this.project = project;
    }

    public void generateBuilder(VirtualFile sourceFile) throws BuilderGenerationException {
        PsiClass sourceClass = findClassForFile(sourceFile);
        PsiClass builderClass = createBuilderClass();
        CommandProcessor processor = CommandProcessor.getInstance();
        processor.executeCommand(project, () -> runWriteAction(sourceClass, builderClass), "WriteBuilder", this);
    }

    private PsiClass findClassForFile(VirtualFile sourceFile) throws BuilderGenerationException {
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

    private PsiClass createBuilderClass() {
        JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(project);
        PsiElementFactory elementFactory = psiFacade.getElementFactory();
        return elementFactory.createClass("Builder");
    }

    private void runWriteAction(PsiClass sourceClass, PsiClass builderClass) {
        Application application = ApplicationManager.getApplication();
        application.runWriteAction(() -> {
            sourceClass.add(builderClass);
        });
    }
}
