package com.soberg.dev;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class GenerateBuilderAction extends AnAction {

    private static final String JAVA_EXTENSION = "java";

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        VirtualFile currentFile = getCurrentOpenFile(project);
        boolean isSourceFile = isSourceFile(currentFile);
        String message = (currentFile != null) ? currentFile.getPath() : "No File";
        message += (isSourceFile ? " is" : " is not") + " a source file";
        String title = "Generate Builder";
        Icon icon = isSourceFile ? Messages.getInformationIcon() : Messages.getErrorIcon();
        Messages.showMessageDialog(project, message, title, icon);
    }

    @Nullable
    private VirtualFile getCurrentOpenFile(@Nullable Project project) {
        Editor textEditor = (project != null) ? FileEditorManager.getInstance(project).getSelectedTextEditor() : null;
        if (textEditor == null) {
            return null;
        }
        Document document = textEditor.getDocument();
        return FileDocumentManager.getInstance().getFile(document);
    }

    private boolean isSourceFile(@Nullable VirtualFile currentFile) {
        return (currentFile != null) && JAVA_EXTENSION.equals(currentFile.getExtension());
    }
}
