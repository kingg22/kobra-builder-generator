package pl.mjedynak.idea.plugins.builder.verifier;

import com.intellij.psi.PsiClass;

public class BuilderVerifier {

    private BuilderVerifier() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static final String SUFFIX = "Builder";

    public static boolean isBuilder(PsiClass psiClass) {
        String className = psiClass.getName();
        return className != null && className.endsWith(SUFFIX);
    }
}
