package pl.mjedynak.idea.plugins.builder.psi;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.mjedynak.idea.plugins.builder.verifier.PsiFieldVerifier;

public class BestConstructorSelector {

    private static final List<ConstructorWithExtraData> constructorsWithEqualParameterCount;
    private static final TreeSet<ConstructorWithExtraData> constructorsWithHigherParameterCount;
    private static final List<ConstructorWithExtraData> constructorsWithLowerParameterCount;

    static {
        constructorsWithEqualParameterCount = Lists.newArrayList();
        constructorsWithHigherParameterCount = Sets.newTreeSet();
        constructorsWithLowerParameterCount = Lists.newArrayList();
    }

    private BestConstructorSelector() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static @Nullable PsiMethod getBestConstructor(
            @NotNull Collection<@NotNull PsiField> psiFieldsToFindInConstructor, @NotNull PsiClass psiClass) {
        int fieldsToFindCount = psiFieldsToFindInConstructor.size();
        createConstructorLists(psiFieldsToFindInConstructor, psiClass);

        computeNumberOfMatchingFields(constructorsWithEqualParameterCount, psiFieldsToFindInConstructor);
        PsiMethod bestConstructor =
                findConstructorWithAllFieldsToFind(constructorsWithEqualParameterCount, fieldsToFindCount);
        if (bestConstructor != null) {
            return bestConstructor;
        }

        computeNumberOfMatchingFields(constructorsWithHigherParameterCount, psiFieldsToFindInConstructor);
        bestConstructor = findConstructorWithAllFieldsToFind(constructorsWithHigherParameterCount, fieldsToFindCount);
        if (bestConstructor != null) {
            return bestConstructor;
        }

        computeNumberOfMatchingFields(constructorsWithLowerParameterCount, psiFieldsToFindInConstructor);
        return findConstructorWithMaximumOfFieldsToFind();
    }

    private static void createConstructorLists(
            @NotNull Collection<PsiField> psiFieldsToFindInConstructor, @NotNull PsiClass psiClass) {
        constructorsWithEqualParameterCount.clear();
        constructorsWithHigherParameterCount.clear();
        constructorsWithLowerParameterCount.clear();
        PsiMethod[] constructors = psiClass.getConstructors();
        for (PsiMethod constructor : constructors) {
            int parameterCount = constructor.getParameterList().getParametersCount();
            if (parameterCount > psiFieldsToFindInConstructor.size()) {
                constructorsWithHigherParameterCount.add(new ConstructorWithExtraData(constructor));
            } else if (parameterCount == psiFieldsToFindInConstructor.size()) {
                constructorsWithEqualParameterCount.add(new ConstructorWithExtraData(constructor));
            } else if (parameterCount >= 0) {
                constructorsWithLowerParameterCount.add(new ConstructorWithExtraData(constructor));
            }
        }
    }

    private static void computeNumberOfMatchingFields(
            @NotNull Iterable<@NotNull ConstructorWithExtraData> constuctorsWithExtraData,
            @NotNull Iterable<@NotNull PsiField> psiFieldsToFindInConstructor) {
        for (ConstructorWithExtraData constructorWithExtraData : constuctorsWithExtraData) {
            int matchingFieldsCount = 0;
            for (PsiField psiField : psiFieldsToFindInConstructor) {
                if (PsiFieldVerifier.checkConstructor(psiField, constructorWithExtraData.getConstructor())) {
                    matchingFieldsCount++;
                }
            }
            constructorWithExtraData.setMatchingFieldsCount(matchingFieldsCount);
        }
    }

    private static @Nullable PsiMethod findConstructorWithAllFieldsToFind(
            @NotNull Iterable<@NotNull ConstructorWithExtraData> constructorsWithExtraData, int fieldsToFindCount) {
        for (ConstructorWithExtraData constructorWithExtraData : constructorsWithExtraData) {
            if (constructorWithExtraData.getMatchingFieldsCount() == fieldsToFindCount) {
                return constructorWithExtraData.getConstructor();
            }
        }
        return null;
    }

    private static @Nullable PsiMethod findConstructorWithMaximumOfFieldsToFind() {
        Iterable<ConstructorWithExtraData> allConstructors = Iterables.concat(
                constructorsWithEqualParameterCount,
                constructorsWithHigherParameterCount,
                constructorsWithLowerParameterCount);
        int matchingFieldCount = -1;
        int parameterCount = 0;
        PsiMethod bestConstructor = null;
        for (ConstructorWithExtraData constructor : allConstructors) {
            if (constructor.getMatchingFieldsCount() > matchingFieldCount
                    || (constructor.getMatchingFieldsCount() == matchingFieldCount
                            && constructor.getParametersCount() < parameterCount)) {
                bestConstructor = constructor.getConstructor();
                matchingFieldCount = constructor.getMatchingFieldsCount();
                parameterCount = constructor.getParametersCount();
            }
        }
        return bestConstructor;
    }

    private static class ConstructorWithExtraData implements Comparable<ConstructorWithExtraData> {
        private final @NotNull PsiMethod constructor;
        private int matchingFieldsCount;

        ConstructorWithExtraData(@NotNull PsiMethod constructor) {
            this.constructor = constructor;
        }

        @Override
        public int compareTo(@NotNull ConstructorWithExtraData other) {
            return Integer.compare(this.getParametersCount(), other.getParametersCount());
        }

        @NotNull
        PsiMethod getConstructor() {
            return constructor;
        }

        int getMatchingFieldsCount() {
            return matchingFieldsCount;
        }

        void setMatchingFieldsCount(int matchingFieldsCount) {
            this.matchingFieldsCount = matchingFieldsCount;
        }

        int getParametersCount() {
            return constructor.getParameterList().getParametersCount();
        }
    }
}
