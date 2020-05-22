package com.soberg.dev.quickbuilder;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.soberg.dev.quickbuilder.di.DaggerQuickBuilderComponent;
import com.soberg.dev.quickbuilder.di.QuickBuilderComponent;
import com.soberg.dev.quickbuilder.environment.CurrentProjectFile;
import com.soberg.dev.quickbuilder.generation.BuilderGenerationCommand;
import com.soberg.dev.quickbuilder.ui.QuickBuilderNotifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
        QuickBuilderNotifier notifier = component.notifier();
        CurrentProjectFile openedFile = component.currentProjectFile();

        // TODO Ask user which class to create Builder for
        List<PsiClass> classes = openedFile.getBuildableClasses();
        if (classes.size() != 1) {
            notifyCannotCreateBuilder(notifier, openedFile);
        } else {
            BuilderGenerationCommand command = component.newCommand();
            command.execute(classes.get(0));
        }
    }

    private QuickBuilderComponent createComponent(Project project) {
        return DaggerQuickBuilderComponent.builder()
                .project(project)
                .build();
    }

    private void notifyCannotCreateBuilder(QuickBuilderNotifier notifier, CurrentProjectFile openedClass) {
        VirtualFile sourceFile = openedClass.getFile();
        String message = (sourceFile != null) ?
                "Cannot create builder for " + sourceFile.getNameWithoutExtension() : "Cannot create builder";
        notifier.notify(message, NotificationType.INFORMATION);
    }
}
