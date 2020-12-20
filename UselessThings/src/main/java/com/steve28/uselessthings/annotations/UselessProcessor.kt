package com.steve28.uselessthings.annotations

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
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
                        else {
                            func(it)
                        }
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
                    if (isUnit) classElement.asType().asTypeName()
                    else f.returnType.asTypeName()


                val funSpec = FunSpec.builder("_${f.simpleName}")
                    .receiver(classElement.asType().asTypeName())
                    .returns(type)

                funSpec.addStatement("val res = this.${f.simpleName}()")
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
}