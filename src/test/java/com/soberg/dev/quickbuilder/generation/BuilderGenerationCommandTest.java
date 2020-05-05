package com.soberg.dev.quickbuilder.generation;


import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.soberg.dev.quickbuilder.ui.QuickBuilderNotifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class BuilderGenerationCommandTest {

    @Mock
    private Project project;
    @Mock
    private BuilderGenerator builderGenerator;
    @Mock
    private ConstructorGenerator constructorGenerator;
    @Mock
    private CommandProcessor processor;
    @Mock
    private Application application;
    @Mock
    private QuickBuilderNotifier notifier;

    @Captor
    private ArgumentCaptor<Runnable> runnableCaptor;

    private BuilderGenerationCommand command;

    @BeforeEach
    public void setup() {
        command = new BuilderGenerationCommand(project, builderGenerator, constructorGenerator, processor, application, notifier);
    }

    @Test
    public void execute() throws BuilderGenerationException {
        PsiClass sourceClass = mock(PsiClass.class);
        executeAndRunWriteAction(sourceClass);

        PsiClass builderClass = mock(PsiClass.class);
        when(builderGenerator.generateBuilderClass(sourceClass)).thenReturn(builderClass);
        PsiMethod constructor = mock(PsiMethod.class);
        when(constructorGenerator.generatePrivateConstructor(sourceClass, builderClass)).thenReturn(constructor);
        runnableCaptor.getValue().run();
        verify(builderGenerator).generateBuilderClass(sourceClass);
        verify(constructorGenerator).generatePrivateConstructor(sourceClass, builderClass);
        verify(sourceClass).add(builderClass);
        verify(sourceClass).add(constructor);
    }

    /**
     * Returns the Runnable sent into {@link Application#runWriteAction(Runnable)}.
     */
    private Runnable executeAndRunWriteAction(PsiClass sourceClass) {
        command.execute(sourceClass);

        // Execute should trigger Application.runWriteAction
        verifyNoInteractions(application);
        verifyNoInteractions(constructorGenerator);
        verifyNoInteractions(builderGenerator);
        verifyNoInteractions(sourceClass);
        verify(processor).executeCommand(eq(project), runnableCaptor.capture(), eq("QuickBuilder-Generator"), eq(command));

        // runWriteAction should trigger the actual builder generation
        runnableCaptor.getValue().run();
        verifyNoInteractions(constructorGenerator);
        verifyNoInteractions(builderGenerator);
        verifyNoInteractions(sourceClass);
        verify(application).runWriteAction(runnableCaptor.capture());
        return runnableCaptor.getValue();
    }

    @Test
    public void executeWithException() throws BuilderGenerationException {
        PsiClass sourceClass = mock(PsiClass.class);
        when(sourceClass.getName()).thenReturn("SourceClass");
        BuilderGenerationException exception = mock(BuilderGenerationException.class);
        when(builderGenerator.generateBuilderClass(sourceClass)).thenThrow(exception);
        executeAndRunWriteAction(sourceClass);

        runnableCaptor.getValue().run();
        verify(builderGenerator).generateBuilderClass(sourceClass);
        verifyNoInteractions(constructorGenerator);
        verify(notifier).notify("Problem generating builder for SourceClass", NotificationType.ERROR);
        verify(exception).printStackTrace();
    }
}