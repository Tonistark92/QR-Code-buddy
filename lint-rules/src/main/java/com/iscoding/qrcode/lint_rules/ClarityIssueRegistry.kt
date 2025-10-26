package com.iscoding.qrcode.lint_rules

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue

/**
 * Registers all custom Lint checks for the QRBuddy project.
 */
class QRBuddyIssueRegistry : IssueRegistry() {

    override val issues: List<Issue>
        get() = listOf(
            WrongThemeDetector.ISSUE,
            WrongComponentDetector.ISSUE
        )

    override val api: Int = CURRENT_API

    override val vendor: Vendor = Vendor(
        vendorName = "QR-Code-buddy",
        feedbackUrl = "https://github.com/Tonistark92/QR-Code-buddy/issues",
        contact = "hanyislam270@gmail.com"
    )
}
