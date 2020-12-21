package com.steve28.uselessthings.annotations

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import kotlin.reflect.jvm.internal.impl.name.FqName
import kotlin.reflect.jvm.internal.impl.builtins.jvm.JavaToKotlinClassMap

@AutoService(Processor::class)
class UselessProcessor: AbstractProcessor() {
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
        val fileBuilder =
                FileSpec.builder("com.uselessthings.generated", "Generated")
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(
                Chaining::class.java.name
        )
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    private fun addFunc(func: FunSpec) = fileBuilder.addFunction(func)

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        val p = process(roundEnv)
        p(Chaining::class.java, ElementKind.CLASS) {
            processChaining(it).forEach { func ->
                addFunc(func)
            }
        }

        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        fileBuilder.build().writeTo(File(kaptKotlinGeneratedDir))

        return true
    }

    private fun process(
            roundEnv: RoundEnvironment
    ): (cls: Class<out Annotation>,
        kind: ElementKind,
        func: (Element) -> Unit) -> Unit {
        fun asdf(
                cls: Class<out Annotation>,
                kind: ElementKind,
                func: (Element) -> Unit
        ) {
            roundEnv.getElementsAnnotatedWith(cls)
                    .forEach {
                        if (it.kind != kind) warning(kind, it)
                        else func(it)
                    }
        }

        return { cls, kind, func -> asdf(cls, kind, func)}
    }

    private fun processChaining(classElement: Element): MutableList<FunSpec> {
        val res: MutableList<FunSpec> = mutableListOf()

        val fieldElement = classElement.enclosedElements
        fieldElement.forEach {
            if (it.kind == ElementKind.METHOD) {
                val f = it as ExecutableElement
                val isUnit =
                    f.returnType.asTypeName().toString() == "kotlin.Unit"

                val type =
                    (if (isUnit) classElement.asType()
                    else f.returnType).asTypeName()


                val funSpec = FunSpec.builder("_${f.simpleName}")
                    .receiver(classElement.asType().asTypeName())
                    .returns(type)

                f.parameters.forEach { v ->
                    val name = v.simpleName
                    val type = v.asType().asTypeName().javaToKotlinType()
                    funSpec.addParameter(name.toString(), type)
                }

                funSpec.addStatement("val res = this.${f.simpleName}(${f.parameters.joinToString(", ") { v -> v.simpleName }})")
                funSpec.addStatement("return ${if (isUnit) "this" else "res"}")

                res.add(funSpec.build())
            }
        }

        return res
    }

    private fun warning(kind: ElementKind, element: Element) {
        processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Only ${kind.name} Are Supported",
                element
        )
    }

    private fun TypeName.javaToKotlinType(): TypeName {
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
}