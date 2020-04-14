package com.soberg.dev.quickbuilder;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.soberg.dev.quickbuilder.generation.BuilderGenerationException;
import com.soberg.dev.quickbuilder.generation.BuilderGenerator;
import com.soberg.dev.quickbuilder.generation.ConstructorGenerator;

class BuilderGenerationCommand {

    private final Project project;
    private final BuilderGenerator builderGenerator;
    private final ConstructorGenerator constructorGenerator;
    private final CommandProcessor processor;
    private final Application application;

    BuilderGenerationCommand(Project project) {
        this.project = project;
        this.builderGenerator = new BuilderGenerator(project);
        this.constructorGenerator = new ConstructorGenerator(project);
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
            PsiMethod constructor = constructorGenerator.generatePrivateConstructor(sourceClass, builderClass);
            sourceClass.add(builderClass);
            sourceClass.add(constructor);
        } catch (BuilderGenerationException e) {
            System.err.println("Problem generating builder for source class " + sourceClass.getName());
            e.printStackTrace();
        }
    }
}
