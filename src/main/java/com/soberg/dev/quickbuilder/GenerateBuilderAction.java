package com.soberg.dev.quickbuilder;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.soberg.dev.quickbuilder.di.DaggerQuickBuilderComponent;
import com.soberg.dev.quickbuilder.di.QuickBuilderComponent;
import com.soberg.dev.quickbuilder.environment.CurrentlyOpenedClass;
import com.soberg.dev.quickbuilder.generation.BuilderGenerationCommand;
import com.soberg.dev.quickbuilder.generation.GenerationConstants;
import com.soberg.dev.quickbuilder.ui.QuickBuilderNotifier;
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
        QuickBuilderNotifier notifier = component.notifier();
        CurrentlyOpenedClass openedClass = component.currentlyOpenedClass();
        PsiClass sourceClass = openedClass.getSourceClass();
        if (sourceClass == null) {
            notifyCannotCreateBuilderError(notifier, openedClass);
        } else if (openedClass.containsInnerClassWithName(GenerationConstants.BUILDER_CLASS_NAME)) {
            notifyBuilderAlreadyExists(notifier, sourceClass);
        } else {
            BuilderGenerationCommand command = component.newCommand();
            command.execute(sourceClass);
        }
    }

    private QuickBuilderComponent createComponent(Project project) {
        return DaggerQuickBuilderComponent.builder()
                .project(project)
                .build();
    }

    private void notifyCannotCreateBuilderError(QuickBuilderNotifier notifier, CurrentlyOpenedClass openedClass) {
        VirtualFile sourceFile = openedClass.getFile();
        String message = (sourceFile != null) ?
                "Cannot create builder for " + sourceFile.getNameWithoutExtension() : "Cannot create builder";
        notifier.notify(message, NotificationType.ERROR);
    }

    private void notifyBuilderAlreadyExists(QuickBuilderNotifier notifier, @NotNull PsiClass sourceClass) {
        String message = sourceClass.getName() + " already has a builder";
        notifier.notify(message, NotificationType.INFORMATION);
    }
}
