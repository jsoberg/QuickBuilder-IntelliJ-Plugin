package com.soberg.dev.quickbuilder.generation;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class ClassGeneratorTest {

    @Mock
    private PsiClass psiClass;
    @Mock
    private PsiModifierList modifiers;
    @Mock
    private PsiElementFactory elementFactory;

    private ClassGenerator classGenerator;

    @BeforeEach
    public void setup() {
        doReturn(psiClass).when(elementFactory).createClass(any());
        classGenerator = new ClassGenerator(elementFactory);
    }

    @Test
    public void generateBuilderClass() throws BuilderGenerationException {
        doReturn(modifiers).when(psiClass).getModifierList();
        classGenerator.generateBuilderClass();
        verify(elementFactory).createClass("Builder");
        verify(modifiers).setModifierProperty(PsiModifier.PUBLIC, true);
        verify(modifiers).setModifierProperty(PsiModifier.STATIC, true);
        verify(modifiers).setModifierProperty(PsiModifier.FINAL, true);
    }

    @Test
    public void generateBuilderClassWithNullModifierList() {
        doReturn(null).when(psiClass).getModifierList();
        BuilderGenerationException exception =
                assertThrows(BuilderGenerationException.class, () -> classGenerator.generateBuilderClass());
        assertThat(exception.getMessage(), is("Problem finding modifier list for Builder class"));
    }
}