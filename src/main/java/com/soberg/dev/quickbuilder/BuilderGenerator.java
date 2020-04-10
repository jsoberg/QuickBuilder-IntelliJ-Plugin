package com.soberg.dev.quickbuilder;

import com.intellij.lang.jvm.JvmModifier;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

public class BuilderGenerator {

    private final Project project;

    public BuilderGenerator(Project project) {
        this.project = project;
    }

    public PsiClass generateBuilderClass(PsiClass sourceClass) throws BuilderGenerationException {
        PsiClass builderClass = createBuilderClass();
        addBuilderFields(sourceClass, builderClass);
        return builderClass;
    }

    private PsiClass createBuilderClass() throws BuilderGenerationException {
        JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(project);
        PsiElementFactory elementFactory = psiFacade.getElementFactory();
        PsiClass builderClass = elementFactory.createClass("Builder");
        addClassModifiers(builderClass);
        return builderClass;
    }

    private void addClassModifiers(PsiClass builderClass) throws BuilderGenerationException {
        PsiModifierList modifiers = builderClass.getModifierList();
        if (modifiers == null) {
            throw new BuilderGenerationException("Problem finding modifier list for Builder class");
        }
        modifiers.setModifierProperty(PsiModifier.PUBLIC, true);
        modifiers.setModifierProperty(PsiModifier.STATIC, true);
        modifiers.setModifierProperty(PsiModifier.FINAL, true);
    }

    private void addBuilderFields(PsiClass sourceClass, PsiClass builderClass) {
        PsiField[] fields = sourceClass.getFields();
        for (PsiField field : fields) {
            builderClass.add(field);
        }
    }
}
