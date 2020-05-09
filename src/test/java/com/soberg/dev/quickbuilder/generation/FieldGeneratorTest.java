package com.soberg.dev.quickbuilder.generation;

import com.intellij.psi.*;
import com.soberg.dev.quickbuilder.ui.settings.SettingsPreferences;
import com.soberg.dev.quickbuilder.ui.settings.SettingsPreferences.FieldModifier;
import com.soberg.dev.quickbuilder.ui.settings.SettingsPreferences.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collection;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class FieldGeneratorTest {

    @Mock
    private PsiElementFactory elementFactory;
    @Mock
    private SettingsPreferences settings;

    private FieldGenerator generator;

    @BeforeEach
    public void setup() {
        generator = new FieldGenerator(elementFactory, settings);
    }

    @Test
    public void generateBuilderFields() throws BuilderGenerationException {
        when(elementFactory.createFieldFromText(any(), any())).thenReturn(mock(PsiField.class));

        PsiModifierList staticModifiers = mock(PsiModifierList.class);
        when(staticModifiers.hasExplicitModifier(PsiModifier.STATIC)).thenReturn(true);
        PsiField field1 = mock(PsiField.class);
        when(field1.getModifierList()).thenReturn(staticModifiers);

        PsiModifierList nonStaticModifiers = mock(PsiModifierList.class);
        when(nonStaticModifiers.hasExplicitModifier(PsiModifier.STATIC)).thenReturn(false);
        PsiField field2 = mock(PsiField.class);
        when(field2.getModifierList()).thenReturn(nonStaticModifiers);
        when(field2.getName()).thenReturn("Field2");
        PsiType field2Type = mock(PsiType.class);
        when(field2.getType()).thenReturn(field2Type);
        when(field2Type.getPresentableText()).thenReturn("Field2Type");
        // Package-Private
        State state = new State(FieldModifier.PACKAGE_PRIVATE);
        when(settings.getPendingState()).thenReturn(state);
        Collection<PsiField> builderFields =
                generator.generateBuilderFields(new PsiField[]{field1, field2});
        assertThat(builderFields.size(), is(1));
        verify(elementFactory).createFieldFromText("Field2Type Field2;", field2);
        // Private
        state = new State(FieldModifier.PRIVATE);
        when(settings.getPendingState()).thenReturn(state);
        builderFields = generator.generateBuilderFields(new PsiField[]{field2});
        assertThat(builderFields.size(), is(1));
        verify(elementFactory).createFieldFromText("private Field2Type Field2;", field2);
        // Public
        state = new State(FieldModifier.PUBLIC);
        when(settings.getPendingState()).thenReturn(state);
        builderFields = generator.generateBuilderFields(new PsiField[]{field2});
        assertThat(builderFields.size(), is(1));
        verify(elementFactory).createFieldFromText("public Field2Type Field2;", field2);
    }

    @Test
    public void generateBuilderFieldsNoModifierList() throws BuilderGenerationException {
        PsiField field1 = mock(PsiField.class);
        when(field1.getModifierList()).thenReturn(null);

        Collection<PsiField> builderFields = generator.generateBuilderFields(new PsiField[]{field1});
        assertThat(builderFields.size(), is(0));
        verifyNoInteractions(elementFactory);
    }

    @Test
    public void generateBuilderFieldsNoFieldName() {
        PsiModifierList modifiers = mock(PsiModifierList.class);
        when(modifiers.hasExplicitModifier(PsiModifier.STATIC)).thenReturn(false);
        PsiField field1 = mock(PsiField.class);
        when(field1.getModifierList()).thenReturn(modifiers);
        when(field1.getName()).thenReturn(null);

        BuilderGenerationException exception =
                assertThrows(BuilderGenerationException.class, () -> generator.generateBuilderFields(new PsiField[]{field1}));
        assertThat(exception.getMessage(), is("Field " + field1 + " has no name"));
    }
}