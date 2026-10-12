package com.kevin.shared.ui.overlay

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.kevin.shared.ui.components.PrimaryButton
import com.kevin.shared.ui.theme.AppTheme

data class TutorialStep(val key: String, val title: String, val text: String)

/** Tracks the root bounds of tagged elements so the spotlight can cut a hole around them (from hr). */
class TutorialAnchors {
    private val rects = mutableStateMapOf<String, Rect>()
    private val requesters = mutableMapOf<String, BringIntoViewRequester>()

    @OptIn(ExperimentalFoundationApi::class)
    fun modifierFor(key: String): Modifier = Modifier
        .onGloballyPositioned { coords -> rects[key] = coords.boundsInRoot() }
        .bringIntoViewRequester(requesterFor(key))

    fun rectOf(key: String): Rect? = rects[key]

    @OptIn(ExperimentalFoundationApi::class)
    fun requesterFor(key: String): BringIntoViewRequester =
        requesters.getOrPut(key) { BringIntoViewRequester() }
}

@Composable
fun rememberTutorialAnchors(): TutorialAnchors = remember { TutorialAnchors() }

fun Modifier.tutorialAnchor(anchors: TutorialAnchors, key: String): Modifier =
    this.then(anchors.modifierFor(key))

/** Hole corner = MaterialTheme.shapes.medium (14dp); a Dp is needed for drawRoundRect. */
private val SpotlightCorner: Dp = 14.dp
private val SpotlightCardMaxWidth: Dp = 480.dp

/** Root coordinates → the overlay's own coordinates (the overlay may not start at the root origin). */
internal fun toOverlaySpace(rectInRoot: Rect, overlayOriginInRoot: Offset): Rect =
    rectInRoot.translate(-overlayOriginInRoot)

/** Target padded by [padPx], clamped to [bounds]; null when nothing of it is on screen. */
internal fun spotlightHole(target: Rect, padPx: Float, bounds: Size): Rect? {
    val hole = Rect(
        left = (target.left - padPx).coerceAtLeast(0f),
        top = (target.top - padPx).coerceAtLeast(0f),
        right = (target.right + padPx).coerceAtMost(bounds.width),
        bottom = (target.bottom + padPx).coerceAtMost(bounds.height),
    )
    return if (hole.width <= 0f || hole.height <= 0f) null else hole
}

/** Below the hole if the card fits there, else above if it fits, else the roomier side. No hole: bottom. */
internal fun spotlightCardAtTop(hole: Rect?, containerHeight: Float, cardHeight: Float, marginPx: Float): Boolean {
    if (hole == null) return false
    val needed = cardHeight + 2f * marginPx
    val spaceBelow = containerHeight - hole.bottom
    val spaceAbove = hole.top
    return when {
        spaceBelow >= needed -> false
        spaceAbove >= needed -> true
        else -> spaceAbove > spaceBelow
    }
}

internal fun nextStepIndex(index: Int, stepCount: Int): Int? =
    if (index + 1 < stepCount) index + 1 else null

/**
 * Dims the screen ([AppTheme.colors.scrimStrong]), cuts a hole around the current step's anchor and
 * shows an explanation card on the side with room. Blocks all touches except the card's buttons.
 * Place it last in a full-screen Box above the content. Seen-state persistence stays in the app.
 * All labels are the app's localized strings; [nextLabel] is composable (so it can call stringResource)
 * and receives the 1-based step and the total.
 */
@Composable
fun TutorialSpotlight(
    steps: List<TutorialStep>,
    anchors: TutorialAnchors,
    visible: Boolean,
    onFinish: () -> Unit,
    skipLabel: String,
    nextLabel: @Composable (step: Int, total: Int) -> String,
    doneLabel: String,
    modifier: Modifier = Modifier,
    contentInsets: WindowInsets = WindowInsets.safeDrawing,
) {
    if (!visible || steps.isEmpty()) return
    var index by remember(steps.size) { mutableIntStateOf(0) }
    val current = index.coerceIn(0, steps.lastIndex)
    val step = steps[current]

    var overlayOrigin by remember { mutableStateOf(Offset.Zero) }
    var areaOrigin by remember { mutableStateOf(Offset.Zero) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    var frozenKey by remember { mutableStateOf<String?>(null) }
    var frozenRect by remember { mutableStateOf<Rect?>(null) }

    LaunchedEffect(step.key, containerSize) {
        anchors.requesterFor(step.key).bringIntoView()
        withFrameNanos { } // one frame for layout to settle after scrolling (hr TutorialOverlay.kt:93)
        frozenRect = anchors.rectOf(step.key) // null if the target isn't composed: dim only, card at the bottom
        frozenKey = step.key
    }

    val spacing = AppTheme.spacing
    val scrim = AppTheme.colors.scrimStrong
    val padPx = with(LocalDensity.current) { spacing.sm.toPx() }
    val margin = spacing.lg
    val holeInOverlay = frozenRect
        ?.takeIf { frozenKey == step.key }
        ?.let { spotlightHole(toOverlaySpace(it, overlayOrigin), padPx, containerSize.toSize()) }
    val holeInArea = holeInOverlay?.translate(overlayOrigin - areaOrigin)
    val isLast = nextStepIndex(current, steps.size) == null

    Box(
        modifier
            .fillMaxSize()
            .onGloballyPositioned {
                overlayOrigin = it.positionInRoot()
                containerSize = it.size
            },
    ) {
        Canvas(
            Modifier
                .matchParentSize()
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) awaitPointerEvent().changes.forEach { it.consume() }
                    }
                },
        ) {
            drawRect(scrim)
            holeInOverlay?.let {
                drawRoundRect(
                    color = Color.Transparent,
                    topLeft = it.topLeft,
                    size = it.size,
                    cornerRadius = CornerRadius(SpotlightCorner.toPx()),
                    blendMode = BlendMode.Clear,
                )
            }
        }
        Layout(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(contentInsets)
                .onGloballyPositioned { areaOrigin = it.positionInRoot() },
            content = {
                SpotlightCard(
                    step = step,
                    actionLabel = if (isLast) doneLabel else nextLabel(current + 1, steps.size),
                    skipLabel = skipLabel,
                    onSkip = onFinish,
                    onAction = {
                        val next = nextStepIndex(current, steps.size)
                        if (next == null) onFinish() else index = next
                    },
                )
            },
        ) { measurables, constraints ->
            val marginPx = margin.roundToPx()
            val width = constraints.maxWidth
            val height = constraints.maxHeight
            val card = measurables.first().measure(
                Constraints(
                    maxWidth = (width - 2 * marginPx).coerceAtLeast(0),
                    maxHeight = (height - 2 * marginPx).coerceAtLeast(0),
                ),
            )
            val atTop = spotlightCardAtTop(holeInArea, height.toFloat(), card.height.toFloat(), marginPx.toFloat())
            layout(width, height) {
                card.place((width - card.width) / 2, if (atTop) marginPx else height - marginPx - card.height)
            }
        }
    }
}

@Composable
private fun SpotlightCard(
    step: TutorialStep,
    actionLabel: String,
    skipLabel: String,
    onSkip: () -> Unit,
    onAction: () -> Unit,
) {
    val spacing = AppTheme.spacing
    val shape = MaterialTheme.shapes.medium
    Column(
        Modifier
            .widthIn(max = SpotlightCardMaxWidth)
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, shape)
            .verticalScroll(rememberScrollState())
            .padding(spacing.lg),
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        Text(step.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
        Text(step.text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onSkip) {
                Text(skipLabel, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            PrimaryButton(text = actionLabel, onClick = onAction)
        }
    }
}
