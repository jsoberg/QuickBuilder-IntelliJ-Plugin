package com.soberg.dev.quickbuilder.generation;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

public class ConstructorGenerator {

    private static final String BUILDER_VAR_NAME = "builder";

    private final PsiElementFactory elementFactory;

    public ConstructorGenerator(Project project) {
        JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(project);
        this.elementFactory = psiFacade.getElementFactory();
    }

    public PsiMethod generatePrivateConstructor(PsiClass sourceClass, PsiClass builderClass) {
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
