package dev.burnoo.ksoup.serializer

import dev.burnoo.ksoup.HtmlDecoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object StringCommentListSerializer : KSerializer<List<String>> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("DataHtmlString", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): List<String> {
        return (decoder as HtmlDecoder).SerializerDecoder()
            .decodeCommentList()
            .map { it.getData() }
    }

    override fun serialize(encoder: Encoder, value: List<String>) {
        error("Serialization is not supported")
    }
}

typealias StringCommentList = @Serializable(StringCommentListSerializer::class) List<String>