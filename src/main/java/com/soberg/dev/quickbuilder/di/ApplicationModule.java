package com.soberg.dev.quickbuilder.di;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    @Provides
    static CommandProcessor providesCommandsProcessor() {
        return CommandProcessor.getInstance();
    }

    @Provides
    static Application providesApplication() {
        return ApplicationManager.getApplication();
    }
}
