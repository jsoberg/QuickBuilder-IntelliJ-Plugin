package com.soberg.dev.quickbuilder;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.soberg.dev.quickbuilder.generation.BuilderGenerationException;
import com.soberg.dev.quickbuilder.generation.BuilderGenerator;

class BuilderGenerationCommand {

    private final Project project;
    private final BuilderGenerator builderGenerator;
    private final CommandProcessor processor;
    private final Application application;

    BuilderGenerationCommand(Project project) {
        this.project = project;
        this.builderGenerator = new BuilderGenerator(project);
        this.processor = CommandProcessor.getInstance();
        this.application = ApplicationManager.getApplication();
    }

    /**
     * Generates a builder class for the specified source class.
     * <p>
     * Note: This action will run within a {@link CommandProcessor}, and within a
     * {@link Application#runWriteAction(Runnable)} call.
     */
    void execute(PsiClass sourceClass) {
        processor.executeCommand(project, () -> runWriteAction(sourceClass), "BuilderGenerator", this);
    }

    private void runWriteAction(PsiClass sourceClass) {
        application.runWriteAction(() -> generateBuilder(sourceClass));
    }

    private void generateBuilder(PsiClass sourceClass) {
        try {
            PsiClass builderClass = builderGenerator.generateBuilderClass(sourceClass);
            sourceClass.add(builderClass);
        } catch (BuilderGenerationException e) {
            System.err.println("Problem generating builder for source class " + sourceClass.getName());
            e.printStackTrace();
        }
    }
}
