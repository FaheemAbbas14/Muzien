package com.tt.muzien.ui.views


/**
 * Created by Faheem Abbas on 18/01/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import com.tt.muzien.R

@SuppressLint("AppCompatCustomView")
class CustomRadioButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val checkIcon: ImageView

    var isChecked: Boolean = false
        set(value) {
            field = value
            updateCheckIcon()
        }

    init {
        orientation = HORIZONTAL
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.custom_payment_item, this, true)

        // Initialize views
        checkIcon = findViewById(R.id.checkIcon)

        // Default state
        isChecked = false

        // Set click listener to toggle state
        setOnClickListener {
            isChecked = !isChecked
        }
    }

    private fun updateCheckIcon() {
        checkIcon.setImageResource(if (isChecked) R.drawable.ic_checked else R.drawable.ic_unchecked)
    }
}
