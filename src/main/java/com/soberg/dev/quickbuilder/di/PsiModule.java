package com.soberg.dev.quickbuilder.di;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElementFactory;
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
}
