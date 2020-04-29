package com.soberg.dev.quickbuilder.generation;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.soberg.dev.quickbuilder.QuickBuilderNotifier;

import javax.inject.Inject;

public class BuilderGenerationCommand {

    private final Project project;
    private final BuilderGenerator builderGenerator;
    private final ConstructorGenerator constructorGenerator;
    private final CommandProcessor processor;
    private final Application application;
    private final QuickBuilderNotifier notifier;

    @Inject
    BuilderGenerationCommand(Project project,
                             BuilderGenerator builderGenerator,
                             ConstructorGenerator constructorGenerator,
                             CommandProcessor processor,
                             Application application,
                             QuickBuilderNotifier notifier) {
        this.project = project;
        this.builderGenerator = builderGenerator;
        this.constructorGenerator = constructorGenerator;
        this.processor = processor;
        this.application = application;
        this.notifier = notifier;
    }

    /**
     * Generates a builder class for the specified source class.
     * <p>
     * Note: This action will run within a {@link CommandProcessor}, and within a
     * {@link Application#runWriteAction(Runnable)} call.
     */
    public void execute(PsiClass sourceClass) {
        processor.executeCommand(project, () -> runWriteAction(sourceClass), "QuickBuilder-Generator", this);
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
            String message = "Problem generating builder for " + sourceClass.getName();
            notifier.notify(message, NotificationType.ERROR);
            e.printStackTrace();
        }
    }
}
