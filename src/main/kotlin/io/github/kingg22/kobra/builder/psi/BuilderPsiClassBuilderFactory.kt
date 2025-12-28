package io.github.kingg22.kobra.builder.psi

import io.github.kingg22.kobra.builder.writer.BuilderContext

public interface BuilderPsiClassBuilderFactory {
    public fun aBuilder(context: BuilderContext): BuilderPsiClassBuilder
    public fun anInnerBuilder(context: BuilderContext): BuilderPsiClassBuilder
}
