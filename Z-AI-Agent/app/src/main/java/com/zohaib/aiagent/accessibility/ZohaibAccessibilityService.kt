package com.zohaib.aiagent.accessibility

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.os.Bundle

class ZohaibAccessibilityService : AccessibilityService() {
    override fun onServiceConnected() {
        super.onServiceConnected()
        AccessibilityBridge.service = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit

    override fun onInterrupt() = Unit

    fun clickVisibleText(target: String): Boolean {
        val root = rootInActiveWindow ?: return false
        val node = findNode(root, target) ?: return false
        return node.performAction(AccessibilityNodeInfo.ACTION_CLICK) ||
            node.parent?.performAction(AccessibilityNodeInfo.ACTION_CLICK) == true
    }

    fun setTextAndSubmit(text: String): Boolean {
        val root = rootInActiveWindow ?: return false
        val editable = findEditable(root) ?: return false
        val args = Bundle().apply {
            putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text)
        }
        val changed = editable.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)
        if (changed) {
            editable.performAction(AccessibilityNodeInfo.ACTION_IME_ENTER)
        }
        return changed
    }

    private fun findNode(root: AccessibilityNodeInfo, target: String): AccessibilityNodeInfo? {
        val wanted = target.trim().lowercase()
        val text = root.text?.toString()?.lowercase().orEmpty()
        val desc = root.contentDescription?.toString()?.lowercase().orEmpty()
        if (text.contains(wanted) || desc.contains(wanted)) return root
        for (i in 0 until root.childCount) {
            root.getChild(i)?.let { child ->
                val found = findNode(child, target)
                if (found != null) return found
            }
        }
        return null
    }

    private fun findEditable(root: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        if (root.isEditable) return root
        for (i in 0 until root.childCount) {
            root.getChild(i)?.let { child ->
                val found = findEditable(child)
                if (found != null) return found
            }
        }
        return null
    }
}
