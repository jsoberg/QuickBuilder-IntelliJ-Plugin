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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class CurrentlyOpenedClassTest {

    @Mock
    private FileEditorManager fileEditorManager;
    @Mock
    private FileDocumentManager fileDocumentManager;
    @Mock
    private PsiManager psiManager;

    @Test
    public void nullTextEditor() {
        when(fileEditorManager.getSelectedTextEditor()).thenReturn(null);
        CurrentlyOpenedClass currentlyOpenedClass = create();

        verify(fileEditorManager).getSelectedTextEditor();
        verifyNoInteractions(fileDocumentManager);
        verifyNoInteractions(psiManager);
        assertThat(currentlyOpenedClass.getFile(), is(nullValue()));
        assertThat(currentlyOpenedClass.getSourceClass(), is(nullValue()));
    }

    private CurrentlyOpenedClass create() {
        return new CurrentlyOpenedClass(fileEditorManager, fileDocumentManager, psiManager);
    }

    @Test
    public void nullGetFile() {
        Editor editor = mock(Editor.class);
        when(fileEditorManager.getSelectedTextEditor()).thenReturn(editor);
        Document document = mock(Document.class);
        when(editor.getDocument()).thenReturn(document);
        when(fileDocumentManager.getFile(document)).thenReturn(null);
        CurrentlyOpenedClass currentlyOpenedClass = create();

        verify(fileEditorManager).getSelectedTextEditor();
        verify(fileDocumentManager).getFile(document);
        verifyNoInteractions(psiManager);
        assertThat(currentlyOpenedClass.getFile(), is(nullValue()));
        assertThat(currentlyOpenedClass.getSourceClass(), is(nullValue()));
    }

    @Test
    public void psiManagerCantFindFile() {
        Editor editor = mock(Editor.class);
        when(fileEditorManager.getSelectedTextEditor()).thenReturn(editor);
        Document document = mock(Document.class);
        when(editor.getDocument()).thenReturn(document);
        VirtualFile virtualFile = mock(VirtualFile.class);
        when(fileDocumentManager.getFile(document)).thenReturn(virtualFile);
        when(psiManager.findFile(virtualFile)).thenReturn(null);
        CurrentlyOpenedClass currentlyOpenedClass = create();

        verify(fileEditorManager).getSelectedTextEditor();
        verify(fileDocumentManager).getFile(document);
        verify(psiManager).findFile(virtualFile);
        assertThat(currentlyOpenedClass.getFile(), is(virtualFile));
        assertThat(currentlyOpenedClass.getSourceClass(), is(nullValue()));
    }

    @Test
    public void psiManagerFindsNonClassOwnerFile() {
        Editor editor = mock(Editor.class);
        when(fileEditorManager.getSelectedTextEditor()).thenReturn(editor);
        Document document = mock(Document.class);
        when(editor.getDocument()).thenReturn(document);
        VirtualFile virtualFile = mock(VirtualFile.class);
        when(fileDocumentManager.getFile(document)).thenReturn(virtualFile);
        when(psiManager.findFile(virtualFile)).thenReturn(mock(PsiFile.class));
        CurrentlyOpenedClass currentlyOpenedClass = create();

        verify(fileEditorManager).getSelectedTextEditor();
        verify(fileDocumentManager).getFile(document);
        verify(psiManager).findFile(virtualFile);
        assertThat(currentlyOpenedClass.getFile(), is(virtualFile));
        assertThat(currentlyOpenedClass.getSourceClass(), is(nullValue()));
    }

    @Test
    public void psiManagerFindsClassOwnerWithMoreThanOneClass() {
        Editor editor = mock(Editor.class);
        when(fileEditorManager.getSelectedTextEditor()).thenReturn(editor);
        Document document = mock(Document.class);
        when(editor.getDocument()).thenReturn(document);
        VirtualFile virtualFile = mock(VirtualFile.class);
        when(fileDocumentManager.getFile(document)).thenReturn(virtualFile);
        PsiClassOwner psiFile = mock(PsiClassOwner.class);
        when(psiFile.getClasses()).thenReturn(new PsiClass[2]);
        when(psiManager.findFile(virtualFile)).thenReturn(psiFile);
        CurrentlyOpenedClass currentlyOpenedClass = create();

        verify(fileEditorManager).getSelectedTextEditor();
        verify(fileDocumentManager).getFile(document);
        verify(psiManager).findFile(virtualFile);
        assertThat(currentlyOpenedClass.getFile(), is(virtualFile));
        assertThat(currentlyOpenedClass.getSourceClass(), is(nullValue()));
    }

    @Test
    public void psiManagerFindsClassOwnerWithOneClass() {
        Editor editor = mock(Editor.class);
        when(fileEditorManager.getSelectedTextEditor()).thenReturn(editor);
        Document document = mock(Document.class);
        when(editor.getDocument()).thenReturn(document);
        VirtualFile virtualFile = mock(VirtualFile.class);
        when(fileDocumentManager.getFile(document)).thenReturn(virtualFile);
        PsiClassOwner psiFile = mock(PsiClassOwner.class);
        PsiClass psiClass = mock(PsiClass.class);
        when(psiFile.getClasses()).thenReturn(new PsiClass[]{psiClass});
        when(psiManager.findFile(virtualFile)).thenReturn(psiFile);
        CurrentlyOpenedClass currentlyOpenedClass = create();

        verify(fileEditorManager).getSelectedTextEditor();
        verify(fileDocumentManager).getFile(document);
        verify(psiManager).findFile(virtualFile);
        assertThat(currentlyOpenedClass.getFile(), is(virtualFile));
        assertThat(currentlyOpenedClass.getSourceClass(), is(psiClass));
    }

    @Test
    public void containsInnerClass() {
        Editor editor = mock(Editor.class);
        when(fileEditorManager.getSelectedTextEditor()).thenReturn(editor);
        Document document = mock(Document.class);
        when(editor.getDocument()).thenReturn(document);
        VirtualFile virtualFile = mock(VirtualFile.class);
        when(fileDocumentManager.getFile(document)).thenReturn(virtualFile);
        PsiClassOwner psiFile = mock(PsiClassOwner.class);
        PsiClass psiClass = mock(PsiClass.class);
        when(psiFile.getClasses()).thenReturn(new PsiClass[]{psiClass});
        when(psiManager.findFile(virtualFile)).thenReturn(psiFile);
        CurrentlyOpenedClass currentlyOpenedClass = create();

        when(psiClass.getInnerClasses()).thenReturn(new PsiClass[0]);
        boolean containsClass = currentlyOpenedClass.containsInnerClassWithName("RightName");
        assertThat(containsClass, is(false));
        PsiClass innerClass = mock(PsiClass.class);
        when(psiClass.getInnerClasses()).thenReturn(new PsiClass[]{innerClass});
        when(innerClass.getName()).thenReturn("WrongName");
        containsClass = currentlyOpenedClass.containsInnerClassWithName("RightName");
        assertThat(containsClass, is(false));
        when(innerClass.getName()).thenReturn("RightName");
        containsClass = currentlyOpenedClass.containsInnerClassWithName("RightName");
        assertThat(containsClass, is(true));
    }
}