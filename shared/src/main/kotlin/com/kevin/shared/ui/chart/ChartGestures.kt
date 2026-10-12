package com.kevin.shared.ui.chart

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import kotlin.math.abs

/**
 * Zoom/pan gestures for x-axis charts (port of hr `ui/shared/ChartGestures.kt:28-130`).
 * - Two+ fingers: [onZoom] (x scale, centroid in px) and [onPan] (dx in px).
 * - One finger: only when [panEnabled]; consumed only after horizontal touch slop, vertical
 *   movement is not consumed (a parent verticalScroll keeps working).
 * - Double tap: [onDoubleTap] (x in px).
 * - Long press + drag: [onScrub] (x in px, null = ended). No panning while scrubbing.
 * Convert px with [plotFraction] (charts in this package draw edge to edge).
 */
fun Modifier.chartZoomPan(
    panEnabled: () -> Boolean,
    onZoom: (factor: Float, anchorX: Float) -> Unit,
    onPan: (dxPx: Float) -> Unit,
    onDoubleTap: (x: Float) -> Unit,
    onScrub: ((x: Float?) -> Unit)? = null,
): Modifier = composed {
    val panEnabledState by rememberUpdatedState(panEnabled)
    val onZoomState by rememberUpdatedState(onZoom)
    val onPanState by rememberUpdatedState(onPan)
    val onDoubleTapState by rememberUpdatedState(onDoubleTap)
    val onScrubState by rememberUpdatedState(onScrub)
    val scrubbing = remember { BooleanArray(1) }
    val scrubEnabled = onScrub != null

    this
        .pointerInput(Unit) {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false)
                var accX = 0f
                var accY = 0f
                var panning = false
                var wasMulti = false
                while (true) {
                    val event = awaitPointerEvent()
                    if (event.changes.none { it.pressed }) break
                    if (scrubbing[0]) {
                        // Scrubbing: no pan/zoom; reset state so a new gesture starts clean.
                        accX = 0f
                        accY = 0f
                        continue
                    }
                    val pressedCount = event.changes.count { it.pressed }
                    if (pressedCount >= 2) {
                        val zoom = event.calculateZoom()
                        val centroid = event.calculateCentroid()
                        val pan = event.calculatePan()
                        if (zoom != 1f && centroid != Offset.Unspecified) onZoomState(zoom, centroid.x)
                        if (pan.x != 0f) onPanState(pan.x)
                        event.changes.forEach { if (it.positionChange() != Offset.Zero) it.consume() }
                        panning = true
                        wasMulti = true
                    } else if (panEnabledState()) {
                        if (wasMulti) {
                            wasMulti = false
                            accX = 0f
                            accY = 0f
                        }
                        val change = event.changes.firstOrNull { it.pressed } ?: break
                        val delta = change.positionChange()
                        if (panning) {
                            if (delta.x != 0f) onPanState(delta.x)
                            change.consume()
                        } else {
                            accX += delta.x
                            accY += delta.y
                            val slop = viewConfiguration.touchSlop
                            if (abs(accX) > slop && abs(accX) > abs(accY)) {
                                panning = true
                                change.consume()
                            } else if (abs(accY) > slop) {
                                break // vertical: leave the gesture to the parent
                            }
                        }
                    } else {
                        // Pan not allowed: ignore one-finger gestures (don't consume, so a parent
                        // verticalScroll stays usable) and wait for another finger.
                        if (wasMulti) {
                            wasMulti = false
                            panning = false
                            accX = 0f
                            accY = 0f
                        }
                    }
                }
            }
        }
        .pointerInput(Unit) {
            detectTapGestures(onDoubleTap = { onDoubleTapState(it.x) })
        }
        .then(
            if (scrubEnabled) Modifier.pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        scrubbing[0] = true
                        onScrubState?.invoke(it.x)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        onScrubState?.invoke(change.position.x)
                    },
                    onDragEnd = {
                        scrubbing[0] = false
                        onScrubState?.invoke(null)
                    },
                    onDragCancel = {
                        scrubbing[0] = false
                        onScrubState?.invoke(null)
                    },
                )
            } else Modifier,
        )
}
