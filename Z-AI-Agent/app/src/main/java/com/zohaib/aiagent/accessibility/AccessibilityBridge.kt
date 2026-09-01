package com.zohaib.aiagent.accessibility

import android.graphics.Rect
import android.view.accessibility.AccessibilityNodeInfo

/**
 * Limited, user-enabled UI automation bridge.
 * It can inspect/click visible UI text in another app; it cannot bypass
 * Android security or guarantee support for every third-party app.
 */
object AccessibilityBridge {
    @Volatile var service: ZohaibAccessibilityService? = null

    fun clickVisibleText(text: String): Boolean =
        service?.clickVisibleText(text) == true

    fun setTextAndSubmit(text: String): Boolean =
        service?.setTextAndSubmit(text) == true
}
