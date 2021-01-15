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


@AutoService(Processor::class)
class UselessProcessor: AbstractProcessor() {
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
        val fileBuilder =
                FileSpec.builder("com.uselessthings.generated", "Generated")
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> = mutableSetOf(
        Chaining::class.java.name
    )

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    private fun addFunc(func: FunSpec) = fileBuilder.addFunction(func)

    override fun process(
            annotations: MutableSet<out TypeElement>?,
            roundEnv: RoundEnvironment
    ): Boolean {
        val p = process(roundEnv)
        p(Chaining::class.java, arrayListOf(ElementKind.CLASS)) {
            UselessProcessorUtils.processChaining(it).forEach { func -> addFunc(func) }
        }

        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        fileBuilder.build().writeTo(File(kaptKotlinGeneratedDir!!))

        return true
    }





    private fun process(roundEnv: RoundEnvironment)
        = { cls: Class<out Annotation>,
          kind: ArrayList<ElementKind>,
          func: (Element) -> Unit ->
            roundEnv.getElementsAnnotatedWith(cls)
                .forEach {
                    if (!kind.contains(it.kind)) warning(processingEnv, kind, it)
                    else func(it)
                }
        }
}