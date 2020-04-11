package com.soberg.dev.quickbuilder;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

public class BuilderGenerator {

    private final PsiElementFactory elementFactory;
    private final BuilderFieldGenerator fieldGenerator;

    public BuilderGenerator(Project project) {
        JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(project);
        this.elementFactory = psiFacade.getElementFactory();
        this.fieldGenerator = new BuilderFieldGenerator(elementFactory);
    }

    public PsiClass generateBuilderClass(PsiClass sourceClass) throws BuilderGenerationException {
        PsiClass builderClass = createBuilderClass();
        PsiField[] sourceFields = sourceClass.getFields();
        fieldGenerator.addBuilderFields(builderClass, sourceFields);
        return builderClass;
    }

    private PsiClass createBuilderClass() throws BuilderGenerationException {
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
}
