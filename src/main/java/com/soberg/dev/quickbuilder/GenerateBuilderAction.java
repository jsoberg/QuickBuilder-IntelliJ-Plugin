package com.soberg.dev.quickbuilder;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.soberg.dev.quickbuilder.di.DaggerQuickBuilderComponent;
import com.soberg.dev.quickbuilder.di.QuickBuilderComponent;
import com.soberg.dev.quickbuilder.environment.CurrentlyOpenedClass;
import com.soberg.dev.quickbuilder.generation.BuilderGenerationCommand;
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
        QuickBuilderComponent component = createComponent(project);
        CurrentlyOpenedClass openedClass = component.currentlyOpenedClass();
        PsiClass sourceClass = openedClass.getSourceClass();
        if (sourceClass != null) {
            BuilderGenerationCommand command = component.newCommand();
            command.execute(sourceClass);
        } else {
            showErrorMessage(project, openedClass);
        }
    }

    private QuickBuilderComponent createComponent(Project project) {
        return DaggerQuickBuilderComponent.builder()
                .project(project)
                .build();
    }

    private void showErrorMessage(Project project, CurrentlyOpenedClass openedClass) {
        VirtualFile sourceFile = openedClass.getFile();
        String message = (sourceFile != null) ? sourceFile.getNameWithoutExtension() + " is not a valid buildable class"
                : "No buildable class found";
        String title = "Generate Builder";
        Messages.showMessageDialog(project, message, title, Messages.getErrorIcon());
    }
}
