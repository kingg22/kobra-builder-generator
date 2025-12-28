package io.github.kingg22.kobra.builder.psi

import org.apache.commons.lang3.StringUtils

public object MethodNameCreator {
    @JvmStatic
    public fun createMethodName(methodPrefix: String?, fieldName: String): String = if (methodPrefix.isNullOrEmpty()) {
        fieldName
    } else {
        "$methodPrefix${StringUtils.capitalize(fieldName)}"
    }
}
