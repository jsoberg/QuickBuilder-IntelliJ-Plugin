package com.soberg.dev.quickbuilder;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

import javax.inject.Inject;

public class QuickBuilderNotifier {

    private final NotificationGroup notificationGroup
            = new NotificationGroup("QuickBuilder Plugin", NotificationDisplayType.BALLOON, true);
    private final Project project;

    @Inject
    QuickBuilderNotifier(Project project) {
        this.project = project;
    }

    public void notify(String message, NotificationType type) {
        Notification notification = notificationGroup.createNotification(message, type);
        notification.notify(project);
    }
}
