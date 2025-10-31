package com.dataxow.ui.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.Paragraph
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import kotlin.collections.listOf
import kotlin.math.absoluteValue
import kotlin.math.ceil

@Composable
fun autoSizeText(
    text: String,
    modifier: Modifier = Modifier,
    acceptableError: Dp = 5.dp,
    maxFontSize: TextUnit = TextUnit.Unspecified,
    color: Color = Color.Unspecified,
    outlineColor: Color = Color.Black,
    outlineWidth: Dp = 1.dp,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    contentAlignment: Alignment? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    val alignment: Alignment = contentAlignment ?: when (textAlign) {
        TextAlign.Left -> Alignment.TopStart
        TextAlign.Right -> Alignment.TopEnd
        TextAlign.Center -> Alignment.Center
        TextAlign.Justify -> Alignment.TopCenter
        TextAlign.Start -> Alignment.TopStart
        TextAlign.End -> Alignment.TopEnd
        else -> Alignment.TopStart
    }
    BoxWithConstraints(modifier = modifier, contentAlignment = alignment) {
        var shrunkFontSize = if (maxFontSize.isSpecified) maxFontSize else 100.sp

        val calculateIntrinsics = @Composable {
            val mergedStyle = style.merge(
                TextStyle(
                    color = color,
                    fontSize = shrunkFontSize,
                    fontWeight = fontWeight,
                    textAlign = textAlign ?: TextAlign.Center,
                    lineHeight = lineHeight,
                    fontFamily = fontFamily,
                    textDecoration = textDecoration,
                    fontStyle = fontStyle,
                    letterSpacing = letterSpacing
                )
            )
            Paragraph(
                text = text,
                style = mergedStyle,
                constraints = Constraints(maxWidth = ceil(LocalDensity.current.run { maxWidth.toPx() }).toInt()),
                density = LocalDensity.current,
                fontFamilyResolver = LocalFontFamilyResolver.current,
                spanStyles = listOf(),
                placeholders = listOf(),
                maxLines = maxLines,
                ellipsis = false,
            )
        }

        var intrinsics = calculateIntrinsics()

        val targetWidth = maxWidth - acceptableError / 2f

        check(targetWidth.isFinite || maxFontSize.isSpecified) { "maxFontSize must be specified if the target with isn't finite!" }

        with(LocalDensity.current) {
            // this loop will attempt to quickly find the correct size font by scaling it by the error
            // it only runs if the max font size isn't specified or the font must be smaller
            // minIntrinsicWidth is "The width for text if all soft wrap opportunities were taken."
            if (maxFontSize.isUnspecified || targetWidth < intrinsics.minIntrinsicWidth.toDp())
                while ((targetWidth - intrinsics.minIntrinsicWidth.toDp()).toPx().absoluteValue.toDp() > acceptableError / 2f) {
                    shrunkFontSize *= targetWidth.toPx() / intrinsics.minIntrinsicWidth
                    intrinsics = calculateIntrinsics()
                }
            // checks if the text fits in the bounds and scales it by 90% until it does
            while (intrinsics.didExceedMaxLines || maxHeight < intrinsics.height.toDp() || maxWidth < intrinsics.minIntrinsicWidth.toDp()) {
                shrunkFontSize *= 0.9f
                intrinsics = calculateIntrinsics()
            }
        }

        if (maxFontSize.isSpecified && shrunkFontSize > maxFontSize) {
            shrunkFontSize = maxFontSize
        }

        // Drawing the text with an outline effect
        val outlineOffset = with(LocalDensity.current) { outlineWidth.toPx() }
        val offsets = listOf(
            -outlineOffset to -outlineOffset,
            outlineOffset to -outlineOffset,
            -outlineOffset to outlineOffset,
            outlineOffset to outlineOffset
        )

        // Draw outline texts
        offsets.forEach { (dx, dy) ->
            Text(
                text = text,
                color = outlineColor,
                fontSize = shrunkFontSize,
                fontStyle = fontStyle,
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                letterSpacing = letterSpacing,
                textDecoration = textDecoration,
                textAlign = textAlign,
                lineHeight = lineHeight,
                modifier = Modifier.offset(dx.dp, dy.dp),
                onTextLayout = onTextLayout,
                maxLines = maxLines,
                style = style
            )
        }

        Text(
            text = text,
            color = color,
            fontSize = shrunkFontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            onTextLayout = onTextLayout,
            maxLines = maxLines,
            style = style
        )
    }
}
