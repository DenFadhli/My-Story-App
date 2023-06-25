package com.submission.mystoryapp.ui.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.submission.mystoryapp.R

class MyEditText : AppCompatEditText, View.OnTouchListener {
    private lateinit var passwordToggleIcon: Drawable
    private lateinit var errorIcon: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        errorIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_error) as Drawable
        passwordToggleIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_visibility) as Drawable
        if (id == R.id.ed_login_password || id == R.id.ed_register_password) {
            setButtonDrawables(endOfTheText = passwordToggleIcon)
        }
        setOnTouchListener(this)

        addTextChangedListener(onTextChanged = { p0, _, _, _ ->
            if (p0.toString().isNotEmpty()) {
                when (id) {
                    R.id.ed_login_email, R.id.ed_register_email -> {
                        if (android.util.Patterns.EMAIL_ADDRESS.matcher(p0.toString()).matches()) {
                            error = null
                            setButtonDrawables(endOfTheText = null)
                        } else {
                            error = resources.getString(R.string.invalid_email)
                            setButtonDrawables(endOfTheText = errorIcon)
                        }
                    }
                    R.id.ed_login_password, R.id.ed_register_password -> {
                        setButtonDrawables(endOfTheText = passwordToggleIcon)
                        if (p0.toString().length < 8) {
                            setError(resources.getString(R.string.invalid_password), null)
                        } else {
                            setError(null, null)
                        }
                    }
                }
            } else {
                when (id) {
                    R.id.ed_login_email, R.id.ed_register_email -> {
                        error = null
                        setButtonDrawables(endOfTheText = null)
                    }
                    R.id.ed_login_password, R.id.ed_register_password -> {
                        setError(null, null)
                        setButtonDrawables(endOfTheText = passwordToggleIcon)
                    }
                    R.id.ed_register_name -> {
                        if (p0.toString().isNotBlank()) {
                            error = null
                            setButtonDrawables(endOfTheText = null)
                        } else {
                            error = resources.getString(R.string.invalid_email)
                            setButtonDrawables(endOfTheText = errorIcon)
                        }
                    }
                }
            }
        })
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        if (id == R.id.ed_login_email || id == R.id.ed_register_email) {
            return false
        } else if (id == R.id.ed_login_password || id == R.id.ed_register_password) {
            if (compoundDrawables[2] != null) {
                val passwordToggleButtonStart: Float
                val passwordToggleButtonEnd: Float
                var isPasswordToggleClicked = false
                if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                    passwordToggleButtonEnd = (passwordToggleIcon.intrinsicWidth + paddingStart).toFloat()
                    when {
                        p1!!.x < passwordToggleButtonEnd -> isPasswordToggleClicked = true
                    }
                } else {
                    passwordToggleButtonStart = (width - paddingEnd - passwordToggleIcon.intrinsicWidth).toFloat()
                    when {
                        p1!!.x > passwordToggleButtonStart -> isPasswordToggleClicked = true
                    }
                }
                if (isPasswordToggleClicked) {
                    when (p1!!.action) {
                        MotionEvent.ACTION_DOWN -> {
                            passwordToggleIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_visibility) as Drawable
                            showPasswordToggle()
                            setSelection(text?.length ?: 0)
                            return true
                        }
                        MotionEvent.ACTION_UP -> {
                            transformationMethod = if (transformationMethod == PasswordTransformationMethod.getInstance()) {
                                passwordToggleIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_invisibility) as Drawable
                                null
                            } else {
                                passwordToggleIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_visibility) as Drawable
                                PasswordTransformationMethod.getInstance()
                            }
                            setSelection(text?.length ?: 0)
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    private fun showPasswordToggle() {
        setButtonDrawables(endOfTheText = passwordToggleIcon)
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = when (id) {
            R.id.ed_register_email -> ContextCompat.getDrawable(context, R.drawable.ic_baseline_alt_email)
            R.id.ed_login_email -> ContextCompat.getDrawable(context, R.drawable.ic_baseline_email)
            R.id.ed_register_password -> ContextCompat.getDrawable(context, R.drawable.ic_baseline_lock_person)
            R.id.ed_login_password -> ContextCompat.getDrawable(context, R.drawable.ic_baseline_lock)
            else -> ContextCompat.getDrawable(context, R.drawable.ic_baseline_person)
        },
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        if (endOfTheText != errorIcon) {
            setCompoundDrawablesWithIntrinsicBounds(startOfTheText, topOfTheText, endOfTheText, bottomOfTheText)
        } else {
            setCompoundDrawablesWithIntrinsicBounds(startOfTheText, topOfTheText, null, bottomOfTheText)
        }
    }
}