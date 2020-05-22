package com.soberg.dev.quickbuilder.environment;


import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassOwner;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class CurrentProjectFileTest {

    @Mock
    private FileEditorManager fileEditorManager;
    @Mock
    private FileDocumentManager fileDocumentManager;
    @Mock
    private PsiManager psiManager;

    @Test
    public void nullTextEditor() {
        when(fileEditorManager.getSelectedTextEditor()).thenReturn(null);
        CurrentProjectFile currentProjectFile = create();

        verify(fileEditorManager).getSelectedTextEditor();
        verifyNoInteractions(fileDocumentManager);
        verifyNoInteractions(psiManager);
        assertThat(currentProjectFile.getFile(), is(nullValue()));
        assertThat(currentProjectFile.getBuildableClasses().isEmpty(), is(true));
    }

    private CurrentProjectFile create() {
        return new CurrentProjectFile(fileEditorManager, fileDocumentManager, psiManager);
    }

    @Test
    public void nullGetFile() {
        Document document = setupDocumentEditorReturns();
        when(fileDocumentManager.getFile(document)).thenReturn(null);
        CurrentProjectFile currentProjectFile = create();

        verify(fileEditorManager).getSelectedTextEditor();
        verify(fileDocumentManager).getFile(document);
        verifyNoInteractions(psiManager);
        assertThat(currentProjectFile.getFile(), is(nullValue()));
        assertThat(currentProjectFile.getBuildableClasses().isEmpty(), is(true));
    }

    private Document setupDocumentEditorReturns() {
        Editor editor = mock(Editor.class);
        when(fileEditorManager.getSelectedTextEditor()).thenReturn(editor);
        Document document = mock(Document.class);
        when(editor.getDocument()).thenReturn(document);
        return document;
    }

    @Test
    public void psiManagerCantFindFile() {
        Document document = setupDocumentEditorReturns();
        VirtualFile virtualFile = mock(VirtualFile.class);
        when(fileDocumentManager.getFile(document)).thenReturn(virtualFile);
        when(psiManager.findFile(virtualFile)).thenReturn(null);
        verifyExpectedCalls(document, virtualFile);
    }

    private void verifyExpectedCalls(Document document, VirtualFile file, PsiClass... classes) {
        CurrentProjectFile currentProjectFile = create();
        verify(fileEditorManager).getSelectedTextEditor();
        verify(fileDocumentManager).getFile(document);
        verify(psiManager).findFile(file);
        assertThat(currentProjectFile.getFile(), is(file));
        if (classes == null || classes.length == 0) {
            assertThat(currentProjectFile.getBuildableClasses().isEmpty(), is(true));
        } else {
            assertThat(currentProjectFile.getBuildableClasses().isEmpty(), is(false));
            assertThat(currentProjectFile.getBuildableClasses(), containsInAnyOrder(classes));
        }
    }

    @Test
    public void psiManagerFindsNonClassOwnerFile() {
        Document document = setupDocumentEditorReturns();
        VirtualFile virtualFile = mock(VirtualFile.class);
        when(fileDocumentManager.getFile(document)).thenReturn(virtualFile);
        when(psiManager.findFile(virtualFile)).thenReturn(mock(PsiFile.class));
        verifyExpectedCalls(document, virtualFile);
    }

    @Test
    public void psiManagerFindsClassOwnerWithMoreThanOneClass() {
        Document document = setupDocumentEditorReturns();
        VirtualFile virtualFile = mock(VirtualFile.class);
        when(fileDocumentManager.getFile(document)).thenReturn(virtualFile);
        PsiClassOwner psiFile = mock(PsiClassOwner.class);
        PsiClass firstClass = mockPsiClass();
        PsiClass secondClass = mockPsiClass();
        when(psiFile.getClasses()).thenReturn(new PsiClass[]{firstClass, secondClass});
        when(psiManager.findFile(virtualFile)).thenReturn(psiFile);
        verifyExpectedCalls(document, virtualFile, firstClass, secondClass);
    }

    private PsiClass mockPsiClass() {
        PsiClass psiClass = mock(PsiClass.class);
        when(psiClass.getInnerClasses()).thenReturn(new PsiClass[0]);
        return psiClass;
    }

    @Test
    public void psiManagerFindsClassOwnerWithOneClass() {
        Document document = setupDocumentEditorReturns();
        VirtualFile virtualFile = mock(VirtualFile.class);
        when(fileDocumentManager.getFile(document)).thenReturn(virtualFile);
        PsiClassOwner psiFile = mock(PsiClassOwner.class);
        PsiClass psiClass = mockPsiClass();
        when(psiFile.getClasses()).thenReturn(new PsiClass[]{psiClass});
        when(psiManager.findFile(virtualFile)).thenReturn(psiFile);
        verifyExpectedCalls(document, virtualFile, psiClass);
    }
}