package com.soberg.dev.quickbuilder.generation;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;

import javax.inject.Inject;

class ClassGenerator {

    private final PsiElementFactory elementFactory;

    @Inject
    ClassGenerator(PsiElementFactory elementFactory) {
        this.elementFactory = elementFactory;
    }

    /**
     * Generates an empty builder {@link PsiClass}.
     */
    PsiClass generateBuilderClass() throws BuilderGenerationException {
        PsiClass builderClass = elementFactory.createClass(GenerationConstants.BUILDER_CLASS_NAME);
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
