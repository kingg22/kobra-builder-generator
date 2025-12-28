package io.github.kingg22.kobra.builder.psi

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod
import io.github.kingg22.kobra.builder.verifier.PsiFieldVerifier
import java.util.TreeSet

public object BestConstructorSelector {
    private val constructorsWithEqualParameterCount: MutableList<ConstructorWithExtraData> = mutableListOf()
    private val constructorsWithHigherParameterCount: TreeSet<ConstructorWithExtraData> = TreeSet()
    private val constructorsWithLowerParameterCount: MutableList<ConstructorWithExtraData> = mutableListOf()

    @JvmStatic
    public fun getBestConstructor(psiFieldsToFindInConstructor: Collection<PsiField>, psiClass: PsiClass): PsiMethod? {
        val fieldsToFindCount = psiFieldsToFindInConstructor.size
        createConstructorLists(psiFieldsToFindInConstructor, psiClass)

        computeNumberOfMatchingFields(constructorsWithEqualParameterCount, psiFieldsToFindInConstructor)
        var bestConstructor: PsiMethod? =
            findConstructorWithAllFieldsToFind(constructorsWithEqualParameterCount, fieldsToFindCount)
        if (bestConstructor != null) {
            return bestConstructor
        }

        computeNumberOfMatchingFields(constructorsWithHigherParameterCount, psiFieldsToFindInConstructor)
        bestConstructor =
            findConstructorWithAllFieldsToFind(constructorsWithHigherParameterCount, fieldsToFindCount)
        if (bestConstructor != null) {
            return bestConstructor
        }

        computeNumberOfMatchingFields(constructorsWithLowerParameterCount, psiFieldsToFindInConstructor)
        return findConstructorWithMaximumOfFieldsToFind()
    }

    private fun createConstructorLists(psiFieldsToFindInConstructor: Collection<PsiField>, psiClass: PsiClass) {
        constructorsWithEqualParameterCount.clear()
        constructorsWithHigherParameterCount.clear()
        constructorsWithLowerParameterCount.clear()
        val constructors = psiClass.constructors
        for (constructor in constructors) {
            val parameterCount = constructor.parameterList.parametersCount
            if (parameterCount > psiFieldsToFindInConstructor.size) {
                constructorsWithHigherParameterCount.add(ConstructorWithExtraData(constructor))
            } else if (parameterCount == psiFieldsToFindInConstructor.size) {
                constructorsWithEqualParameterCount.add(ConstructorWithExtraData(constructor))
            } else if (parameterCount >= 0) {
                constructorsWithLowerParameterCount.add(ConstructorWithExtraData(constructor))
            }
        }
    }

    private fun computeNumberOfMatchingFields(
        constuctorsWithExtraData: Iterable<ConstructorWithExtraData>,
        psiFieldsToFindInConstructor: Iterable<PsiField>,
    ) {
        for (constructorWithExtraData in constuctorsWithExtraData) {
            var matchingFieldsCount = 0
            for (psiField in psiFieldsToFindInConstructor) {
                if (PsiFieldVerifier.checkConstructor(psiField, constructorWithExtraData.constructor)) {
                    matchingFieldsCount++
                }
            }
            constructorWithExtraData.matchingFieldsCount = matchingFieldsCount
        }
    }

    private fun findConstructorWithAllFieldsToFind(
        constructorsWithExtraData: Iterable<ConstructorWithExtraData>,
        fieldsToFindCount: Int,
    ): PsiMethod? {
        for (constructorWithExtraData in constructorsWithExtraData) {
            if (constructorWithExtraData.matchingFieldsCount == fieldsToFindCount) {
                return constructorWithExtraData.constructor
            }
        }
        return null
    }

    private fun findConstructorWithMaximumOfFieldsToFind(): PsiMethod? {
        val allConstructors = listOf(
            constructorsWithEqualParameterCount,
            constructorsWithHigherParameterCount,
            constructorsWithLowerParameterCount,
        ).flatten()
        var matchingFieldCount = -1
        var parameterCount = 0
        var bestConstructor: PsiMethod? = null
        for (constructor in allConstructors) {
            if (constructor.matchingFieldsCount > matchingFieldCount ||
                (
                    constructor.matchingFieldsCount == matchingFieldCount &&
                        constructor.parametersCount < parameterCount
                    )
            ) {
                bestConstructor = constructor.constructor
                matchingFieldCount = constructor.matchingFieldsCount
                parameterCount = constructor.parametersCount
            }
        }
        return bestConstructor
    }

    private class ConstructorWithExtraData(val constructor: PsiMethod) : Comparable<ConstructorWithExtraData> {
        var matchingFieldsCount: Int = 0

        override operator fun compareTo(other: ConstructorWithExtraData): Int =
            this.parametersCount.compareTo(other.parametersCount)

        val parametersCount: Int
            get() = constructor.parameterList.parametersCount
    }
}
