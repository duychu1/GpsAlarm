package com.ruicomp.gpsalarm.feature.ads


import android.app.Activity
import android.widget.FrameLayout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.common.control.manager.AdmobManager
import com.ruicomp.gpsalarm.utils.dlog

@Composable
fun BannerAdvertView(
    modifier: Modifier = Modifier,
    adUnitId: List<String>, // Pass your Ad Unit ID
) {
    val context = LocalContext.current
    val activity = context as? Activity // AdmobManager needs an Activity

    dlog("BannerAdvertView recomposing. adUnitId hash: ${adUnitId.hashCode()}, content: $adUnitId")
    // Get an instance of your AdmobManager
    // This could be a singleton, injected via Hilt, or created as needed
    // For simplicity, assuming a singleton here:
    // Or however you access it

    if (activity == null) {
        // Handle cases where context might not be an Activity (though unlikely for a full screen)
        // You could show a placeholder or log an error
        return
    }

    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { ctx ->
            dlog("BannerAdvertView: factory")
            // Create a FrameLayout to serve as the adContainer
            FrameLayout(ctx).apply {
                // Optionally set an ID if your AdmobManager relies on it for some reason,
                // though for this specific function, it just adds the AdView to this FrameLayout.
                // this.id = View.generateViewId()
            }
        },
        update = { frameLayoutContainer ->
            dlog("BannerAdvertView: update")
            // This 'update' block is called when the AdUnitId or other relevant state changes,
            // or during recomposition.
            // Call your AdmobManager's method to load the ad into the FrameLayout.
            AdmobManager.getInstance().loadAlternateBanner(activity, adUnitId, frameLayoutContainer, null)
        }
    )
}