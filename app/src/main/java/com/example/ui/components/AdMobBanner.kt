package com.example.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdConfig {
    const val APP_ID = "ca-app-pub-2648040211095579~5935711707"
    // User's Real Ad Units
    const val BANNER_AD_UNIT_ID = "ca-app-pub-2648040211095579/4269697239"
    const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-2648040211095579/7059512107"

    // Google Official Test Ad Units (Fallbacks when new account is pending approval)
    const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    const val TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
}

@Composable
fun AdMobBanner(
    modifier: Modifier = Modifier,
    adUnitId: String = AdConfig.BANNER_AD_UNIT_ID
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    this.adUnitId = adUnitId
                    adListener = object : com.google.android.gms.ads.AdListener() {
                        override fun onAdFailedToLoad(error: LoadAdError) {
                            android.util.Log.w("AdMobBanner", "Real ad failed (${error.message}), falling back to test ad")
                            // Fallback to test banner if real account is still in initial review
                            val fallbackAdView = AdView(context).apply {
                                setAdSize(AdSize.BANNER)
                                this.adUnitId = AdConfig.TEST_BANNER_AD_UNIT_ID
                                loadAd(AdRequest.Builder().build())
                            }
                            this@apply.removeAllViews()
                            this@apply.addView(fallbackAdView)
                        }
                    }
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}

object InterstitialAdHelper {
    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false

    fun loadAd(context: Context) {
        if (interstitialAd != null || isLoading) return
        isLoading = true
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            AdConfig.INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isLoading = false
                    android.util.Log.d("InterstitialAd", "Real ad loaded successfully")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    android.util.Log.w("InterstitialAd", "Real ad failed (${loadAdError.message}), trying test ad")
                    // Fallback to test interstitial if account is pending
                    InterstitialAd.load(
                        context,
                        AdConfig.TEST_INTERSTITIAL_AD_UNIT_ID,
                        adRequest,
                        object : InterstitialAdLoadCallback() {
                            override fun onAdLoaded(testAd: InterstitialAd) {
                                interstitialAd = testAd
                                isLoading = false
                                android.util.Log.d("InterstitialAd", "Test ad loaded successfully")
                            }

                            override fun onAdFailedToLoad(testError: LoadAdError) {
                                interstitialAd = null
                                isLoading = false
                                android.util.Log.e("InterstitialAd", "Both ads failed: ${testError.message}")
                            }
                        }
                    )
                }
            }
        )
    }

    fun showAd(context: Context, onDismiss: () -> Unit = {}) {
        val activity = context.findActivity()
        val ad = interstitialAd
        if (ad != null && activity != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadAd(context)
                    onDismiss()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    interstitialAd = null
                    loadAd(context)
                    onDismiss()
                }
            }
            ad.show(activity)
        } else {
            loadAd(context)
            onDismiss()
        }
    }

    private fun Context.findActivity(): Activity? {
        var currentContext = this
        while (currentContext is ContextWrapper) {
            if (currentContext is Activity) {
                return currentContext
            }
            currentContext = currentContext.baseContext
        }
        return null
    }
}
