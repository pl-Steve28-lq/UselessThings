package com.steve28.uselessthings.annotations

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.lang.reflect.Executable
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.tools.Diagnostic
import kotlin.reflect.jvm.internal.impl.builtins.jvm.JavaToKotlinClassMap
import kotlin.reflect.jvm.internal.impl.name.FqName

fun TypeName.javaToKotlinType(): TypeName {
    return when (this) {
        is ParameterizedTypeName -> {
            (rawType.javaToKotlinType() as ClassName).parameterizedBy(
                    typeArguments.map { it.javaToKotlinType() }.toTypedArray().toList()
            )
        }
        is WildcardTypeName -> {
            outTypes[0].javaToKotlinType()
        }
        else -> {
            val className = JavaToKotlinClassMap.INSTANCE
                    .mapJavaToKotlin(FqName(toString()))
                    ?.asSingleFqName()?.asString()
            return className?.let { ClassName.bestGuess(it) } ?: this
        }
    }
}

fun warning(
        processingEnv: ProcessingEnvironment,
        kind: ArrayList<ElementKind>,
        element: Element
) = processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Only ${kind.joinToString(", ") { v -> v.name }} Are Supported",
                element
        )

fun ExecutableElement.paramPairs(): ArrayList<Pair<String, TypeName>> {
    val res = arrayListOf<Pair<String, TypeName>>()
    this.parameters.forEach { v ->
        res.add(Pair(
                v.simpleName.toString(),
                v.asType().asTypeName().javaToKotlinType()
        ))
    }
    return res
}

fun ExecutableElement.type(): TypeName = this.returnType.asTypeName()