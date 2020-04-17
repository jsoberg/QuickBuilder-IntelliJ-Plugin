package com.soberg.dev.quickbuilder.generation;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiMethod;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class BuildMethodGeneratorTest {

    @Mock
    private PsiElementFactory elementFactory;
    @Mock
    private PsiClass sourceClass;
    @Mock
    private PsiClass builderClass;

    private BuildMethodGenerator buildMethodGenerator;

    @BeforeEach
    void setup() {
        buildMethodGenerator = new BuildMethodGenerator(elementFactory);
    }

    @Test
    void addBuildMethod() {
        PsiMethod buildMethod = mock(PsiMethod.class);
        when(elementFactory.createMethodFromText(any(), eq(builderClass))).thenReturn(buildMethod);
        when(sourceClass.getName()).thenReturn("SourceClassName");
        buildMethodGenerator.addBuildMethod(sourceClass, builderClass);

        ArgumentCaptor<String> methodTextCaptor = ArgumentCaptor.forClass(String.class);
        verify(elementFactory).createMethodFromText(methodTextCaptor.capture(), eq(builderClass));
        String methodText = methodTextCaptor.getValue();
        assertThat(methodText, is("public SourceClassName build() {return new SourceClassName(this);}"));

        verify(builderClass).add(buildMethod);
    }
}