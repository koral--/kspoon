package dev.burnoo.ksoup

enum class HtmlTextMode {
    Text,
    InnerHtml,
    OuterHtml,
    Data // script, style, comment
    ;

    internal companion object {

        fun fromAttribute(attribute: String?) = when (attribute) {
            "text" -> Text
            "innerHtml", "html" -> InnerHtml
            "outerHtml" -> OuterHtml
            "data" -> Data
            else -> null
        }
    }
}
