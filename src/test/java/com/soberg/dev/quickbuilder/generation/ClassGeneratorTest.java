package com.soberg.dev.quickbuilder.generation;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class ClassGeneratorTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Mock
    private PsiClass psiClass;
    @Mock
    private PsiModifierList modifiers;
    @Mock
    private PsiElementFactory elementFactory;

    private ClassGenerator classGenerator;

    @Before
    public void setup() {
        doReturn(modifiers).when(psiClass).getModifierList();
        doReturn(psiClass).when(elementFactory).createClass(any());
        classGenerator = new ClassGenerator(elementFactory);
    }

    @Test
    public void generateBuilderClass() throws BuilderGenerationException {
        classGenerator.generateBuilderClass();
        verify(elementFactory).createClass("Builder");
        verify(modifiers).setModifierProperty(PsiModifier.PUBLIC, true);
        verify(modifiers).setModifierProperty(PsiModifier.STATIC, true);
        verify(modifiers).setModifierProperty(PsiModifier.FINAL, true);
    }

    @Test
    public void generateBuilderClassWithNullModifierList() throws BuilderGenerationException {
        exception.expect(BuilderGenerationException.class);
        exception.expectMessage("Problem finding modifier list for Builder class");
        doReturn(null).when(psiClass).getModifierList();
        classGenerator.generateBuilderClass();
    }
}