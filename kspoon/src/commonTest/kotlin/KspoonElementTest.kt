package dev.burnoo.ksoup

import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.select.Elements
import dev.burnoo.ksoup.annotation.Selector
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlin.test.Test

@OptIn(ExperimentalSerializationApi::class)
class KspoonElementTest {

    @Test
    fun shouldParseElements() {
        @Serializable
        data class Model(
            @Selector("ul > li")
            @Contextual
            val elements: Elements,
        )

        val body =
            """
            <ul>
                <li>1</li>
                <li>2</li>
                <li>3</li>
            </ul>
            """.trimIndent()
        val model = Kspoon.decodeFromString<Model>(body)

        model.elements.map { it.outerHtml() } shouldBe listOf(
            "<li>1</li>",
            "<li>2</li>",
            "<li>3</li>",
        )
    }

    @Test
    fun shouldParseFirstElement() {
        @Serializable
        data class Model(
            @Selector("ul > li")
            @Contextual
            val element: Element,
        )

        val body =
            """
            <ul>
                <li>1</li>
                <li>2</li>
                <li>3</li>
            </ul>
            """.trimIndent()
        val model = Kspoon.decodeFromString<Model>(body)

        model.element.outerHtml() shouldBe "<li>1</li>"
    }

    @Test
    fun shouldGetNullElement() {
        @Serializable
        data class Model(
            @Selector("p")
            @Contextual
            val element: Element?,
        )

        val body = ""
        val model = Kspoon.decodeFromString<Model>(body)

        model shouldBe Model(null)
    }

    @Test
    fun shouldParseListOfElements() {
        @Serializable
        data class Model(
            @Selector("ul > li")
            val elements: List<@Contextual Element>,
        )

        val body =
            """
            <ul>
                <li>1</li>
                <li>2</li>
                <li>3</li>
            </ul>
            """.trimIndent()
        val model = Kspoon.decodeFromString<Model>(body)

        model.elements.map { it.outerHtml() } shouldBe listOf(
            "<li>1</li>",
            "<li>2</li>",
            "<li>3</li>",
        )
    }
}