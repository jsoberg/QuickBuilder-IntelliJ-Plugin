package com.soberg.dev.quickbuilder;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

public class BuilderGenerator {

    private final BuilderClassGenerator classGenerator;
    private final BuilderFieldGenerator fieldGenerator;

    public BuilderGenerator(Project project) {
        JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(project);
        PsiElementFactory elementFactory = psiFacade.getElementFactory();
        this.classGenerator = new BuilderClassGenerator(elementFactory);
        this.fieldGenerator = new BuilderFieldGenerator(elementFactory);
    }

    public PsiClass generateBuilderClass(PsiClass sourceClass) throws BuilderGenerationException {
        PsiField[] sourceFields = sourceClass.getFields();
        PsiClass builderClass = classGenerator.generateBuilderClass();
        fieldGenerator.addBuilderFields(builderClass, sourceFields);
        return builderClass;
    }
}
