package com.ruicomp.gpsalarm.feature.outer
// PremiumActivity.kt (Simplified relevant parts)
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.common.control.interfaces.PurchaseCallback
import com.common.control.manager.PurchaseManager
import com.ruicomp.gpsalarm.MyApp
import com.ruicomp.gpsalarm.R
import com.ruicomp.gpsalarm.databinding.ActivityUpgradePremiumBinding
import androidx.lifecycle.lifecycleScope
import com.ruicomp.gpsalarm.AppSession
import com.ruicomp.gpsalarm.Constants
import com.ruicomp.gpsalarm.MainActivity
import com.ruicomp.gpsalarm.datastore2.DataStoreKeys
import com.ruicomp.gpsalarm.datastore2.DataStorePreferences
import com.ruicomp.gpsalarm.utils.dlog
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.isInitialized
import kotlin.jvm.java
import kotlin.text.indexOf
import kotlin.text.isNullOrEmpty

class PremiumActivity : BaseActivityNonBinding(), PurchaseCallback {

    private val dataStorePreferences: DataStorePreferences by inject()
    private lateinit var binding: ActivityUpgradePremiumBinding
    private lateinit var purchaseManagerInstance: PurchaseManager
    private val premiumProductId = MyApp.PRODUCT_LIFETIME

    private var isFromHome = false
    private var isDismiss = AppSession.isDismissPremium
    private var isShowLessPremium = isDismiss

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpgradePremiumBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.f0f0f0)
        isFromHome = intent.getBooleanExtra(Constants.KEY_FROM_HOME, false)
        // Assuming your PurchaseManager in CommonLib handles both or you've chosen one.
        purchaseManagerInstance = PurchaseManager.getInstance() // Or PurchaseManagerInApp.getInstance()
        purchaseManagerInstance.setCallback(this) // Register to get purchase results

        if (isGotoNext()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }
        setupView()
        setupClickListeners()
    }

    private fun isGotoNext(): Boolean {
        if (isFromHome) return false
        if (purchaseManagerInstance.isPurchased()) return true
        if (isDismiss) return true
        return false
    }

    private fun setupView() {
        setupTermsTextView(binding.tvTerms)
        updatePremiumButtonText()
        lifecycleScope.launch {
            dataStorePreferences.getLong(DataStoreKeys.LAST_PREMIUM_SHOWN_AT).let {
                dlog("Collect: Last Premium Shown At: $it")
                isShowLessPremium = (it ?: 0L) > 0L
                binding.btnDismiss.text = getString(if (isShowLessPremium) R.string.show_more else R.string.show_less)
                if (isShowLessPremium && !isFromHome) {
                    dataStorePreferences.saveLong(DataStoreKeys.LAST_PREMIUM_SHOWN_AT, System.currentTimeMillis())
                }
            }
        }
    }


    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener {
            if (!isFromHome) {
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
        }
        binding.btnBuyPremium.setOnClickListener {
            // Implement premium purchase logic
            Log.d("PremiumActivity", "btnBuyPremium clicked for product: $premiumProductId")

            // Launch the purchase flow using the instance from PurchaseManager
            purchaseManagerInstance.launchPurchase(this@PremiumActivity, premiumProductId)
        }
        binding.btnDismiss.setOnClickListener {
            lifecycleScope.launch {
                isShowLessPremium = !isShowLessPremium
                binding.btnDismiss.text = getString(if (isShowLessPremium) R.string.show_more else R.string.show_less)
                val timestamp = if (isShowLessPremium) System.currentTimeMillis() else 0L
                dataStorePreferences.saveLong(DataStoreKeys.LAST_PREMIUM_SHOWN_AT, timestamp)
            }
        }
    }

    private fun updatePremiumButtonText() {
        // You might want to check if the user is already premium
        // This check would depend on how isPurchased() is implemented in PurchaseManager
        // For instance, if isPurchased() checks a specific product or any premium product.
        if (purchaseManagerInstance.isPurchased()) { // Or a more specific check
            binding.btnBuyPremium.text = getString(R.string.premium_activated)
            binding.btnBuyPremium.isEnabled = false
        } else {
            val price = purchaseManagerInstance.getPriceInApp(premiumProductId) // Or getPriceSub
            if (!price.isNullOrEmpty()) {
                binding.btnBuyPremium.text = getString(R.string.buy_premium_forever, price)
            } else {
                binding.btnBuyPremium.text = getString(R.string.buy_premium_forever, "") // Fallback or loading state
            }
            binding.btnBuyPremium.isEnabled = true
        }
    }


    // --- PurchaseCallback Implementation ---
    override fun purchaseSuccess() {
        Toast.makeText(this, "Premium Purchase Successful!", Toast.LENGTH_LONG).show()
        Log.d("PremiumActivity", "Purchase successful!")
        // TODO:
        // 1. Grant premium features to the user.
        // 2. Persist the premium status locally (e.g., SharedPreferences, DataStore)
        //    so the app remembers it across sessions.
        // 3. Update UI accordingly (e.g., disable buy button, show premium content).
        updatePremiumButtonText() // Refresh button state
    }

    override fun purchaseFail() {
        Toast.makeText(this, "Premium Purchase Failed. Please try again.", Toast.LENGTH_LONG).show()
        Log.e("PremiumActivity", "Purchase failed!")
        // Optionally, provide more specific error information if available from BillingResult
    }

    private fun setupTermsTextView(textView: TextView) {
        val fullText = getString(R.string.terms_agreement)
        val spannableString = SpannableString(fullText)

        // Define the link texts - ensure these exactly match the substrings in your strings.xml
        val termOfUseText = getString(R.string.term_of_use)
        val privacyPolicyText = getString(R.string.privacy_policy)
        val restoreText = getString(R.string.restore)

        // --- Term of Use Link ---
        val termOfUseStartIndex = fullText.indexOf(termOfUseText)
        if (termOfUseStartIndex != -1) {
            val termOfUseEndIndex = termOfUseStartIndex + termOfUseText.length
            val termOfUseClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    // TODO: Handle Term of Use click (e.g., open a URL or an Activity)
//                    val browserIntent =
//                        Intent(Intent.ACTION_VIEW, Uri.parse("https://www.example.com/terms"))
//                    startActivity(browserIntent)
                    Toast.makeText(this@PremiumActivity, "$termOfUseText clicked", Toast.LENGTH_SHORT).show()

                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = true // Ensure underline
                    // Optionally, set link color (TextView's default link color is often used)
                    // ds.color = Color.BLUE
                }
            }
            spannableString.setSpan(termOfUseClickableSpan, termOfUseStartIndex, termOfUseEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            // If ClickableSpan doesn't automatically underline in all themes/versions,
            // you can add an UnderlineSpan explicitly, though it's often not needed.
            // spannableString.setSpan(UnderlineSpan(), termOfUseStartIndex, termOfUseEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        // --- Privacy Policy Link ---
        val privacyPolicyStartIndex = fullText.indexOf(privacyPolicyText)
        if (privacyPolicyStartIndex != -1) {
            val privacyPolicyEndIndex = privacyPolicyStartIndex + privacyPolicyText.length
            val privacyPolicyClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
//                    PrivacyPolicyActivity.startActivity(this@PremiumActivity)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = true
                }
            }
            spannableString.setSpan(privacyPolicyClickableSpan, privacyPolicyStartIndex, privacyPolicyEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        // --- Restore Link ---
        val restoreStartIndex = fullText.indexOf(restoreText)
        if (restoreStartIndex != -1) {
            val restoreEndIndex = restoreStartIndex + restoreText.length
            val restoreClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    // TODO: Handle Restore click (e.g., call your restore purchases logic)
                    // For example:
                    // purchaseManagerInstance.restorePurchases() // If you have such a method
                     Toast.makeText(this@PremiumActivity, "Restore Clicked", Toast.LENGTH_SHORT).show()
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = true
                }
            }
            spannableString.setSpan(restoreClickableSpan, restoreStartIndex, restoreEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance() // VERY IMPORTANT! Makes links clickable
        textView.highlightColor = getColor(R.color.light_gray) // Optional: Disables the default highlight color on click
    }


    override fun onResume() {
        super.onResume()
        // It's good practice to ensure the callback is set,
        // and also to refresh purchase status and product details
        // in case they changed while the activity was paused.
        if (::purchaseManagerInstance.isInitialized) {
            purchaseManagerInstance.setCallback(this)
            updatePremiumButtonText() // Update UI based on potentially new data
        }
    }
}