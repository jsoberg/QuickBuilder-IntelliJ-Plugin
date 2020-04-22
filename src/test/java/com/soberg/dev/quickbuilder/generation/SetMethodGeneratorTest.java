package com.soberg.dev.quickbuilder.generation;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class SetMethodGeneratorTest {

    @Mock
    private PsiField field1;
    @Mock
    private PsiType fieldType1;

    @Mock
    private PsiClass builderClass;
    @Mock
    private PsiElementFactory elementFactory;

    private SetMethodGenerator generator;

    @BeforeEach
    public void setup() {
        generator = new SetMethodGenerator(elementFactory);
    }

    @Test
    public void addSetMethods() throws BuilderGenerationException {
        when(field1.getName()).thenReturn("field1");
        when(fieldType1.getPresentableText()).thenReturn("Field1Type");
        when(field1.getType()).thenReturn(fieldType1);

        PsiField field2 = mock(PsiField.class);
        when(field2.getName()).thenReturn("field2");
        PsiType type2 = mock(PsiType.class);
        when(type2.getPresentableText()).thenReturn("Field2Type");
        when(field2.getType()).thenReturn(type2);
        generator.addSetMethods(builderClass, Arrays.asList(field1, field2));

        ArgumentCaptor<String> textCaptor = ArgumentCaptor.forClass(String.class);
        verify(elementFactory).createMethodFromText(textCaptor.capture(), eq(field1));
        String method1Text = textCaptor.getValue();
        String expectedMethod1Text =
                "public Builder setField1(Field1Type field1) { "
                        + "this.field1 = field1; "
                        + "return this; "
                        + "}";
        assertThat(method1Text, is(expectedMethod1Text));

        verify(elementFactory).createMethodFromText(textCaptor.capture(), eq(field2));
        String method2Text = textCaptor.getValue();
        String expectedMethod2Text =
                "public Builder setField2(Field2Type field2) { "
                        + "this.field2 = field2; "
                        + "return this; "
                        + "}";
        assertThat(method2Text, is(expectedMethod2Text));
    }

    @Test
    public void generateSetMethodForSingleCharacterFieldName() throws BuilderGenerationException {
        when(field1.getName()).thenReturn("a");
        when(fieldType1.getPresentableText()).thenReturn("Field1Type");
        when(field1.getType()).thenReturn(fieldType1);
        generator.addSetMethods(builderClass, Collections.singletonList(field1));

        ArgumentCaptor<String> textCaptor = ArgumentCaptor.forClass(String.class);
        verify(elementFactory).createMethodFromText(textCaptor.capture(), eq(field1));
        String method1Text = textCaptor.getValue();
        String expectedMethod1Text =
                "public Builder setA(Field1Type a) { "
                        + "this.a = a; "
                        + "return this; "
                        + "}";
        assertThat(method1Text, is(expectedMethod1Text));
    }

    @Test
    public void fieldWithNoNameThrowsException() {
        when(field1.getName()).thenReturn(null);
        BuilderGenerationException exception = assertThrows(BuilderGenerationException.class,
                () -> generator.addSetMethods(builderClass, Collections.singletonList(field1)));
        assertThat(exception.getMessage(), is("Could not find name for field " + field1));
    }
}