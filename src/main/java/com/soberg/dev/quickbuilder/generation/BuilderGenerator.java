package com.soberg.dev.quickbuilder.generation;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;

import javax.inject.Inject;
import java.util.Collection;

public class BuilderGenerator {

    private final ClassGenerator classGenerator;
    private final FieldGenerator fieldGenerator;
    private final SetMethodGenerator setMethodGenerator;
    private final BuildMethodGenerator buildMethodGenerator;

    @Inject
    BuilderGenerator(ClassGenerator classGenerator,
                     FieldGenerator fieldGenerator,
                     SetMethodGenerator setMethodGenerator,
                     BuildMethodGenerator buildMethodGenerator) {
        this.classGenerator = classGenerator;
        this.fieldGenerator = fieldGenerator;
        this.setMethodGenerator = setMethodGenerator;
        this.buildMethodGenerator = buildMethodGenerator;
    }

    public PsiClass generateBuilderClass(PsiClass sourceClass) throws BuilderGenerationException {
        PsiField[] sourceFields = sourceClass.getFields();
        PsiClass builderClass = classGenerator.generateBuilderClass();
        Collection<PsiField> builderFields = fieldGenerator.generateBuilderFields(sourceFields);
        addFieldsToBuilderClass(builderClass, builderFields);
        setMethodGenerator.addSetMethods(builderClass, builderFields);
        buildMethodGenerator.addBuildMethod(sourceClass, builderClass);
        return builderClass;
    }

    private void addFieldsToBuilderClass(PsiClass builderClass, Collection<PsiField> builderFields) {
        for (PsiField field : builderFields) {
            builderClass.add(field);
        }
    }
}
