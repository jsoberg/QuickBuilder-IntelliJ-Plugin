package com.soberg.dev.quickbuilder;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.soberg.dev.quickbuilder.environment.CurrentlyOpenedClass;
import org.jetbrains.annotations.NotNull;

public class GenerateBuilderAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        CurrentlyOpenedClass openedClass = new CurrentlyOpenedClass(project);
        PsiClass sourceClass = openedClass.getSourceClass();
        if (sourceClass != null) {
            generateBuilder(project, sourceClass);
        } else {
            VirtualFile sourceFile = openedClass.getFile();
            String message = (sourceFile != null) ? sourceFile.getNameWithoutExtension() + " is not a valid buildable class"
                    : "No buildable class found";
            String title = "Generate Builder";
            Messages.showMessageDialog(project, message, title, Messages.getErrorIcon());
        }
    }

    private void generateBuilder(Project project, PsiClass sourceClass) {
        try {
            PsiClass builderClass = new BuilderGenerator(project)
                    .generateBuilderClass(sourceClass);
            CommandProcessor processor = CommandProcessor.getInstance();
            processor.executeCommand(project, () -> runWriteAction(sourceClass, builderClass), "WriteBuilder", this);
        } catch (BuilderGenerationException e) {
            // TODO: Handle exception
        }
    }

    private void runWriteAction(PsiClass sourceClass, PsiClass builderClass) {
        Application application = ApplicationManager.getApplication();
        application.runWriteAction(() -> {
            sourceClass.add(builderClass);
        });
    }
}
