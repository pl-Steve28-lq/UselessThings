package com.steve28.uselessthings.annotations

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.asTypeName
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement

object UselessProcessorUtils {
    fun processChaining(classElement: Element): MutableList<FunSpec> {
        val res: MutableList<FunSpec> = mutableListOf()

        val fieldElement = classElement.enclosedElements
        fieldElement.forEach {
            if (it.kind == ElementKind.METHOD) {
                val f = it as ExecutableElement
                val isUnit =
                        f.type().toString() == "kotlin.Unit"

                val type =
                        (if (isUnit) classElement.asType()
                        else f.returnType).asTypeName()


                val funSpec = FunSpec.builder("_${f.simpleName}")
                        .receiver(classElement.asType().asTypeName())
                        .returns(type)

                f.paramPairs().forEach { v ->
                    funSpec.addParameter(v.first, v.second)
                }

                funSpec.addStatement("val res = this.${f.simpleName}(${f.parameters.joinToString(", ") { v -> v.simpleName }})")
                funSpec.addStatement("return ${if (isUnit) "this" else "res"}")

                res.add(funSpec.build())
            }
        }

        return res
    }
}