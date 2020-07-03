package com.soberg.dev.quickbuilder.di;

import com.intellij.openapi.project.Project;
import com.soberg.dev.quickbuilder.environment.CurrentlyOpenedClass;
import com.soberg.dev.quickbuilder.generation.BuilderGenerationCommand;
import com.soberg.dev.quickbuilder.ui.QuickBuilderNotifier;
import dagger.BindsInstance;
import dagger.Component;

@Component(modules = {
        ApplicationModule.class,
        PsiModule.class
})
public interface QuickBuilderComponent {

    BuilderGenerationCommand newCommand();

    CurrentlyOpenedClass currentlyOpenedClass();

    QuickBuilderNotifier notifier();

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder project(Project project);

        QuickBuilderComponent build();
    }
}
