package com.soberg.dev.quickbuilder.generation;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class BuilderGeneratorTest {

    @Mock
    private ClassGenerator classGenerator;
    @Mock
    private FieldGenerator fieldGenerator;
    @Mock
    private SetMethodGenerator setMethodGenerator;
    @Mock
    private BuildMethodGenerator buildMethodGenerator;

    private BuilderGenerator builderGenerator;

    @BeforeEach
    public void setup() {
        builderGenerator = new BuilderGenerator(classGenerator, fieldGenerator, setMethodGenerator, buildMethodGenerator);
    }

    @Test
    public void generateBuilderClass() throws BuilderGenerationException {
        PsiClass sourceClass = mock(PsiClass.class);
        PsiClass builderClass = mock(PsiClass.class);
        when(classGenerator.generateBuilderClass()).thenReturn(builderClass);
        PsiField field = mock(PsiField.class);
        PsiField[] fieldArray = new PsiField[]{field};
        when(sourceClass.getFields()).thenReturn(fieldArray);
        List<PsiField> fieldList = Arrays.asList(fieldArray);
        when(fieldGenerator.generateBuilderFields(any()))
                .thenReturn(fieldList);
        builderGenerator.generateBuilderClass(sourceClass);

        verify(classGenerator).generateBuilderClass();
        verify(fieldGenerator).generateBuilderFields(fieldArray);
        verify(builderClass).add(field);
        verify(setMethodGenerator).addSetMethods(builderClass, fieldList);
        verify(buildMethodGenerator).addBuildMethod(sourceClass, builderClass);
    }
}