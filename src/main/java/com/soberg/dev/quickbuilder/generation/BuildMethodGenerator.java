package com.soberg.dev.quickbuilder.generation;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiMethod;

import javax.inject.Inject;

class BuildMethodGenerator {

    private final PsiElementFactory elementFactory;

    @Inject
    BuildMethodGenerator(PsiElementFactory elementFactory) {
        this.elementFactory = elementFactory;
    }

    /**
     * Adds a build() method to the specified builder class, returning the specified source class' type.
     */
    void addBuildMethod(PsiClass sourceClass, PsiClass builderClass) {
        PsiMethod buildMethod = generateBuildMethod(sourceClass, builderClass);
        builderClass.add(buildMethod);
    }

    private PsiMethod generateBuildMethod(PsiClass sourceClass, PsiClass builderClass) {
        String sourceClassName = sourceClass.getName();
        return elementFactory.createMethodFromText(
                "public " + sourceClassName + " build() {\n"
                        + "    return new " + sourceClassName + "(this)\n"
                        + "}"
                , builderClass);
    }
}
