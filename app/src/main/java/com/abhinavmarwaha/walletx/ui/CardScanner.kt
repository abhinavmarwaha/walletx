
import com.abhinavmarwaha.walletx.R
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import io.card.payment.CardIOActivity
import io.card.payment.CreditCard
import io.card.payment.i18n.locales.LocalizedStringsList

class CardScanner : AppCompatActivity() {
    private var mManualToggle: CheckBox? = null
    private var mEnableExpiryToggle: CheckBox? = null
    private var mScanExpiryToggle: CheckBox? = null
    private var mCvvToggle: CheckBox? = null
    private var mPostalCodeToggle: CheckBox? = null
    private var mPostalCodeNumericOnlyToggle: CheckBox? = null
    private var mCardholderNameToggle: CheckBox? = null
    private var mSuppressManualToggle: CheckBox? = null
    private var mSuppressConfirmationToggle: CheckBox? = null
    private var mSuppressScanToggle: CheckBox? = null
    private var mResultLabel: TextView? = null
    private var mResultImage: ImageView? = null
    private var mResultCardTypeImage: ImageView? = null
    private var autotestMode = false
    private var numAutotestsPassed = 0
    private var mUseCardIOLogoToggle: CheckBox? = null
    private var mShowPayPalActionBarIconToggle: CheckBox? = null
    private var mKeepApplicationThemeToggle: CheckBox? = null
    private var mLanguageSpinner: Spinner? = null
    private var mUnblurEdit: EditText? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.card_scaner)
        mManualToggle = findViewById<View>(R.id.force_manual) as CheckBox
        mEnableExpiryToggle = findViewById<View>(R.id.gather_expiry) as CheckBox
        mScanExpiryToggle = findViewById<View>(R.id.scan_expiry) as CheckBox
        mCvvToggle = findViewById<View>(R.id.gather_cvv) as CheckBox
        mPostalCodeToggle = findViewById<View>(R.id.gather_postal_code) as CheckBox
        mPostalCodeNumericOnlyToggle = findViewById<View>(R.id.postal_code_numeric_only) as CheckBox
        mCardholderNameToggle = findViewById<View>(R.id.gather_cardholder_name) as CheckBox
        mSuppressManualToggle = findViewById<View>(R.id.suppress_manual) as CheckBox
        mSuppressConfirmationToggle = findViewById<View>(R.id.suppress_confirmation) as CheckBox
        mSuppressScanToggle = findViewById<View>(R.id.detect_only) as CheckBox
        mUseCardIOLogoToggle = findViewById<View>(R.id.use_card_io_logo) as CheckBox
        mShowPayPalActionBarIconToggle =
            findViewById<View>(R.id.show_paypal_action_bar_icon) as CheckBox
        mKeepApplicationThemeToggle = findViewById<View>(R.id.keep_application_theme) as CheckBox
        mLanguageSpinner = findViewById<View>(R.id.language) as Spinner
        mUnblurEdit = findViewById<View>(R.id.unblur) as EditText
        mResultLabel = findViewById<View>(R.id.result) as TextView
        mResultImage = findViewById<View>(R.id.result_image) as ImageView
        mResultCardTypeImage = findViewById<View>(R.id.result_card_type_image) as ImageView
        val version = findViewById<View>(R.id.version) as TextView
        version.text = """
            card.io library: ${CardIOActivity.sdkVersion()}
            Build date: ${CardIOActivity.sdkBuildDate()}
            """.trimIndent()
        setScanExpiryEnabled()
        setupLanguageList()
    }

    private fun setScanExpiryEnabled() {
        mScanExpiryToggle!!.isEnabled = mEnableExpiryToggle!!.isChecked
    }

    fun onExpiryToggle(v: View?) {
        setScanExpiryEnabled()
    }

    fun onScan(pressed: View?) {
        val intent = Intent(this, CardIOActivity::class.java)
            .putExtra(CardIOActivity.EXTRA_NO_CAMERA, mManualToggle!!.isChecked)
            .putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, mEnableExpiryToggle!!.isChecked)
            .putExtra(CardIOActivity.EXTRA_SCAN_EXPIRY, mScanExpiryToggle!!.isChecked)
            .putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, mCvvToggle!!.isChecked)
            .putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, mPostalCodeToggle!!.isChecked)
            .putExtra(
                CardIOActivity.EXTRA_RESTRICT_POSTAL_CODE_TO_NUMERIC_ONLY,
                mPostalCodeNumericOnlyToggle!!.isChecked
            )
            .putExtra(
                CardIOActivity.EXTRA_REQUIRE_CARDHOLDER_NAME,
                mCardholderNameToggle!!.isChecked
            )
            .putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, mSuppressManualToggle!!.isChecked)
            .putExtra(CardIOActivity.EXTRA_USE_CARDIO_LOGO, mUseCardIOLogoToggle!!.isChecked)
            .putExtra(
                CardIOActivity.EXTRA_LANGUAGE_OR_LOCALE,
                mLanguageSpinner!!.selectedItem as String
            )
            .putExtra(
                CardIOActivity.EXTRA_USE_PAYPAL_ACTIONBAR_ICON,
                mShowPayPalActionBarIconToggle!!.isChecked
            )
            .putExtra(
                CardIOActivity.EXTRA_KEEP_APPLICATION_THEME,
                mKeepApplicationThemeToggle!!.isChecked
            )
            .putExtra(CardIOActivity.EXTRA_GUIDE_COLOR, Color.GREEN)
            .putExtra(
                CardIOActivity.EXTRA_SUPPRESS_CONFIRMATION,
                mSuppressConfirmationToggle!!.isChecked
            )
            .putExtra(CardIOActivity.EXTRA_SUPPRESS_SCAN, mSuppressScanToggle!!.isChecked)
            .putExtra(CardIOActivity.EXTRA_RETURN_CARD_IMAGE, true)
        try {
            val unblurDigits = mUnblurEdit!!.text.toString().toInt()
            intent.putExtra(CardIOActivity.EXTRA_UNBLUR_DIGITS, unblurDigits)
        } catch (ignored: NumberFormatException) {
        }
        startActivityForResult(intent, REQUEST_SCAN)
    }

    fun onAutotest(v: View?) {
        Log.i(
            TAG, """


 ============================== 
successfully completed $numAutotestsPassed tests
beginning new test run
"""
        )
        val intent = Intent(this, CardIOActivity::class.java)
            .putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, false)
            .putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false)
            .putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false)
            .putExtra(CardIOActivity.EXTRA_REQUIRE_CARDHOLDER_NAME, false)
            .putExtra("debug_autoAcceptResult", true)
        startActivityForResult(intent, REQUEST_AUTOTEST)
        autotestMode = true
    }

    public override fun onStop() {
        super.onStop()
        mResultLabel!!.text = ""
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.v(
            TAG,
            "onActivityResult($requestCode, $resultCode, $data)"
        )
        var outStr = String()
        var cardTypeImage: Bitmap? = null
        if ((requestCode == REQUEST_SCAN || requestCode == REQUEST_AUTOTEST) && data != null && data.hasExtra(
                CardIOActivity.EXTRA_SCAN_RESULT
            )
        ) {
            val result = data.getParcelableExtra<CreditCard>(CardIOActivity.EXTRA_SCAN_RESULT)
            if (result != null) {
                outStr += """
                    Card number: ${result.redactedCardNumber}
                    
                    """.trimIndent()
                val cardType = result.cardType
                cardTypeImage = cardType.imageBitmap(this)
                outStr += """Card type: ${cardType.name} cardType.getDisplayName(null)=${
                    cardType.getDisplayName(
                        null
                    )
                }
"""
                if (mEnableExpiryToggle!!.isChecked) {
                    outStr += """
                        Expiry: ${result.expiryMonth}/${result.expiryYear}
                        
                        """.trimIndent()
                }
                if (mCvvToggle!!.isChecked) {
                    outStr += """
                        CVV: ${result.cvv}
                        
                        """.trimIndent()
                }
                if (mPostalCodeToggle!!.isChecked) {
                    outStr += """
                        Postal Code: ${result.postalCode}
                        
                        """.trimIndent()
                }
                if (mCardholderNameToggle!!.isChecked) {
                    outStr += """
                        Cardholder Name: ${result.cardholderName}
                        
                        """.trimIndent()
                }
            }
            if (autotestMode) {
                numAutotestsPassed++
                Handler().postDelayed({ onAutotest(null) }, 500)
            }
        } else if (resultCode == RESULT_CANCELED) {
            autotestMode = false
        }
        val card = CardIOActivity.getCapturedCardImage(data)
        mResultImage!!.setImageBitmap(card)
        mResultCardTypeImage!!.setImageBitmap(cardTypeImage)
        Log.i(TAG, "Set result: $outStr")
        mResultLabel!!.text = outStr
    }

    private fun setupLanguageList() {
        val languages: MutableList<String> = ArrayList()
        for (locale in LocalizedStringsList.ALL_LOCALES) {
            languages.add(locale.name)
        }
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line, languages
        )
        mLanguageSpinner!!.adapter = adapter
        mLanguageSpinner!!.setSelection(adapter.getPosition("en"))
    }

    companion object {
        protected val TAG = CardScanner::class.java.simpleName
        private const val REQUEST_SCAN = 100
        private const val REQUEST_AUTOTEST = 200
    }
}