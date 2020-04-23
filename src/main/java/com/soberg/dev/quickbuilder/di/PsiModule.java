package com.soberg.dev.quickbuilder.di;

import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiManager;
import dagger.Module;
import dagger.Provides;

@Module
public class PsiModule {
    @Provides
    static JavaPsiFacade providesPsiFacade(Project project) {
        return JavaPsiFacade.getInstance(project);
    }

    @Provides
    static PsiElementFactory providesElementFactory(JavaPsiFacade psiFacade) {
        return psiFacade.getElementFactory();
    }

    @Provides
    static FileEditorManager fileEditorManager(Project project) {
        return FileEditorManager.getInstance(project);
    }

    @Provides
    static FileDocumentManager fileDocumentManager() {
        return FileDocumentManager.getInstance();
    }

    @Provides
    static PsiManager psiManager(Project project) {
        return PsiManager.getInstance(project);
    }
}
