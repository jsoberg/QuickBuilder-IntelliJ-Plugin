package com.soberg.dev.quickbuilder;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
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
import org.jetbrains.annotations.NotNull;

public class GenerateBuilderAction extends AnAction {

    private final NotificationGroup notificationGroup = new NotificationGroup("QuickBuilder Plugin", NotificationDisplayType.BALLOON, true);

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
        if (sourceClass == null) {
            notifyCannotCreateBuilderError(project, openedClass);
        } else if (openedClass.containsInnerClassWithName(GenerationConstants.BUILDER_CLASS_NAME)) {
            notifyBuilderAlreadyExists(project, sourceClass);
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

    private void notifyCannotCreateBuilderError(Project project, CurrentlyOpenedClass openedClass) {
        VirtualFile sourceFile = openedClass.getFile();
        String message = (sourceFile != null) ?
                "Cannot create builder for " + sourceFile.getNameWithoutExtension() : "Cannot create builder";
        Notification notification = notificationGroup.createNotification(message, NotificationType.ERROR);
        notification.notify(project);
    }

    private void notifyBuilderAlreadyExists(Project project, @NotNull PsiClass sourceClass) {
        String message = sourceClass.getName() + " already has a builder";
        Notification notification = notificationGroup.createNotification(message, NotificationType.INFORMATION);
        notification.notify(project);
    }
}
