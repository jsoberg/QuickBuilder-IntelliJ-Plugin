package com.soberg.dev;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GenerateBuilderAction extends AnAction {

    private static final String JAVA_EXTENSION = "java";

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(isSourceFileOpen(project));
    }

    private boolean isSourceFileOpen(Project project) {
        VirtualFile currentFile = getCurrentOpenFile(project);
        return currentFile != null && JAVA_EXTENSION.equals(currentFile.getExtension());
    }

    @Nullable
    private VirtualFile getCurrentOpenFile(Project project) {
        Editor textEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (textEditor == null) {
            return null;
        }
        Document document = textEditor.getDocument();
        return FileDocumentManager.getInstance().getFile(document);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // Using the event, create and show a dialog
        Project currentProject = event.getProject();
        StringBuffer dlgMsg = new StringBuffer(event.getPresentation().getText() + " Selected!");
        String dlgTitle = event.getPresentation().getDescription();
        // If an element is selected in the editor, add info about it.
        Navigatable nav = event.getData(CommonDataKeys.NAVIGATABLE);
        if (nav != null) {
            dlgMsg.append(String.format("\nSelected Element: %s", nav.toString()));
        }
        Messages.showMessageDialog(currentProject, dlgMsg.toString(), dlgTitle, Messages.getInformationIcon());
    }
}
