package com.soberg.dev.quickbuilder.generation;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

import java.util.Collection;

public class BuilderGenerator {

    private final ClassGenerator classGenerator;
    private final FieldGenerator fieldGenerator;
    private final SetMethodGenerator setMethodGenerator;

    public BuilderGenerator(Project project) {
        JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(project);
        PsiElementFactory elementFactory = psiFacade.getElementFactory();
        this.classGenerator = new ClassGenerator(elementFactory);
        this.fieldGenerator = new FieldGenerator(elementFactory);
        this.setMethodGenerator = new SetMethodGenerator(elementFactory);
    }

    public PsiClass generateBuilderClass(PsiClass sourceClass) throws BuilderGenerationException {
        PsiField[] sourceFields = sourceClass.getFields();
        PsiClass builderClass = classGenerator.generateBuilderClass();
        Collection<PsiField> builderFields = fieldGenerator.generateBuilderFields(sourceFields);
        addFieldsToBuilderClass(builderClass, builderFields);
        setMethodGenerator.addSetMethods(builderClass, builderFields);
        return builderClass;
    }

    private void addFieldsToBuilderClass(PsiClass builderClass, Collection<PsiField> builderFields) {
        for (PsiField field : builderFields) {
            builderClass.add(field);
        }
    }
}
