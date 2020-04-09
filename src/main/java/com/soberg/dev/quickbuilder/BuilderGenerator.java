package com.soberg.dev.quickbuilder;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

public class BuilderGenerator {

    private final Project project;

    public BuilderGenerator(Project project) {
        this.project = project;
    }

    public PsiClass generateBuilderClass(PsiClass sourceFile) throws BuilderGenerationException {
        return createBuilderClass();
        // TODO: Perform additional operations on the builder class.
    }

    private PsiClass createBuilderClass() {
        JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(project);
        PsiElementFactory elementFactory = psiFacade.getElementFactory();
        return elementFactory.createClass("Builder");
    }
}
