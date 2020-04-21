package com.soberg.dev.quickbuilder.generation;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;

import javax.inject.Inject;

class ConstructorGenerator {

    private static final String BUILDER_VAR_NAME = "builder";

    private final PsiElementFactory elementFactory;

    @Inject
    ConstructorGenerator(PsiElementFactory elementFactory) {
        this.elementFactory = elementFactory;
    }

    /**
     * Pre-Condition: Assumes that the specified builderClass's fields are a subset of specified sourceClass' fields.
     * Assumes that these fields share names.
     * <p>
     * Generates a private constructor for the specified source class. This constructor will set all fields from the
     * provided builder class, in the manner "this.field = builder.field".
     */
    PsiMethod generatePrivateConstructor(PsiClass sourceClass, PsiClass builderClass) {
        String constructorText = generateConstructorText(sourceClass, builderClass);
        return elementFactory.createMethodFromText(constructorText, sourceClass);
    }

    private String generateConstructorText(PsiClass sourceClass, PsiClass builderClass) {
        StringBuilder methodBuilder = new StringBuilder();
        appendConstructorFirstLine(methodBuilder, sourceClass, builderClass);
        appendFieldInitialization(methodBuilder, builderClass);
        appendConstructorLastLine(methodBuilder);
        return methodBuilder.toString();
    }

    private void appendConstructorFirstLine(StringBuilder methodBuilder, PsiClass sourceClass, PsiClass builderClass) {
        // Example: private SourceClass(BuilderClass builder) {
        methodBuilder.append("private ").append(sourceClass.getName()).append("(")
                .append(builderClass.getName()).append(" ").append(BUILDER_VAR_NAME).append(") {");
    }

    private void appendFieldInitialization(StringBuilder methodBuilder, PsiClass builderClass) {
        for (PsiField field : builderClass.getFields()) {
            String name = field.getName();
            // Example: this.fieldName = builder.fieldName;
            methodBuilder.append("this.").append(name).append(" = ")
                    .append(BUILDER_VAR_NAME).append('.').append(name).append(';');
        }
    }

    private void appendConstructorLastLine(StringBuilder methodBuilder) {
        methodBuilder.append('}');
    }
}
