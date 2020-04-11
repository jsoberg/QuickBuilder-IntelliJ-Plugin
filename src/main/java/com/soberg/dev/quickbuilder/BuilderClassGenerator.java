package com.soberg.dev.quickbuilder;

import com.intellij.psi.*;

class BuilderClassGenerator {

    private final PsiElementFactory elementFactory;

    BuilderClassGenerator(PsiElementFactory elementFactory) {
        this.elementFactory = elementFactory;
    }

    PsiClass generateBuilderClass() throws BuilderGenerationException {
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
