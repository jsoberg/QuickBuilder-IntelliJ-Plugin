package com.soberg.dev.quickbuilder.generation;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
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
public class ConstructorGeneratorTest {

    @Mock
    private PsiClass sourceClass;
    @Mock
    private PsiClass builderClass;
    @Mock
    private PsiElementFactory elementFactory;

    private ConstructorGenerator generator;

    @BeforeEach
    public void setup() {
        this.generator = new ConstructorGenerator(elementFactory);
    }

    @Test
    public void generatePrivateConstructor() {
        when(sourceClass.getName()).thenReturn("SourceClass");
        when(builderClass.getName()).thenReturn("BuilderClass");
        PsiField firstField = mock(PsiField.class);
        when(firstField.getName()).thenReturn("field1");
        PsiField secondField = mock(PsiField.class);
        when(secondField.getName()).thenReturn("field2");
        when(builderClass.getFields()).thenReturn(new PsiField[]{firstField, secondField});
        generator.generatePrivateConstructor(sourceClass, builderClass);

        ArgumentCaptor<String> constructorTextCaptor = ArgumentCaptor.forClass(String.class);
        verify(elementFactory).createMethodFromText(constructorTextCaptor.capture(), eq(sourceClass));
        String constructor = constructorTextCaptor.getValue();
        String expected = "private SourceClass(BuilderClass builder) {"
                + "this.field1 = builder.field1;"
                + "this.field2 = builder.field2;"
                + "}";
        assertThat(constructor, is(expected));
    }
}