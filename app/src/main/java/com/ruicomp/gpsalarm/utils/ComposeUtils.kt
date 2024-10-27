package com.ruicomp.gpsalarm.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.Flow

/**
 * Remembers in the Composition a flow that only emits data when `lifecycle` is
 * at least in `minActiveState`. That's achieved using the `Flow.flowWithLifecycle` operator.
 *
 * Explanation: If flows with operators in composable functions are not remembered, operators
 * will _always_ be called and applied on every recomposition.
 */
@Composable
fun <T> rememberFlowWithLifecycle(
    flow: Flow<T>,
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED
): Flow<T> = remember(flow, lifecycle) {
    flow.flowWithLifecycle(
        lifecycle = lifecycle,
        minActiveState = minActiveState
    )
}

/**
 * Apply a placeholder highlighting to a [Composable] is the [visible] property is set to true.
 *
 * @param visible whether or not to apply a placeholder to the parent [Composable]
 */
//fun Modifier.fadePlaceholder(visible: Boolean): Modifier = composed {
//    placeholder(
//        visible = visible,
//        highlight = com.google.accompanist.placeholder.PlaceholderHighlight.Companion.fade(),
//    )
//}
//
//@Composable
//fun requireActivity(): ComponentActivity {
//    var currentContext = CompositionLocal.current
//    while (currentContext is ContextWrapper) {
//        if (currentContext is ComponentActivity) {
//            return currentContext
//        }
//        currentContext = currentContext.baseContext
//    }
//
//    error("Composable not attached to an activity.")
//}
