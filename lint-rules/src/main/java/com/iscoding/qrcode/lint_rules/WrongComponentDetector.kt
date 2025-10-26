package com.iscoding.qrcode.lint_rules

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.LintFix
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import org.jetbrains.uast.UCallExpression

/**
 * Detector to enforce usage of QRBuddy custom UI components
 * instead of directly using Material3 components.
 */
class WrongComponentDetector : Detector(), Detector.UastScanner {

    override fun getApplicableUastTypes() = listOf(UCallExpression::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler {
        return object : UElementHandler() {
            override fun visitCallExpression(node: UCallExpression) {
                val qualifiedName = node.resolve()?.let {
                    it.containingClass?.qualifiedName + "." + it.name
                }

                replacements[qualifiedName]?.let { replacement ->
                    val fix = LintFix.create()
                        .replace()
                        .text(node.methodIdentifier?.name)
                        .with("com.iscoding.qrcode.systemdesign.components.$replacement") // ← your core-ui equivalent module path
                        .shortenNames()
                        .range(context.getLocation(node.methodIdentifier ?: node))
                        .build()

                    context.report(
                        issue = ISSUE,
                        location = context.getLocation(node),
                        message = "Use $replacement from the :systemdesign module instead of Material3.",
                        quickfixData = fix.autoFix()
                    )
                }
            }
        }
    }

    companion object {
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "WrongComponentUsage",
            briefDescription = "Incorrect component used.",
            explanation = """
                Use components from the `:systemdesign` module instead of direct Material3 components 
                to maintain consistent design across the QRBuddy app.
            """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 6,
            severity = Severity.ERROR,
            implementation = Implementation(
                WrongComponentDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )

        private val replacements = mapOf(
//            "androidx.compose.material3.ButtonKt.Button" to "QRBuddyButton",
            "androidx.compose.material3.CardKt.Card" to "QRBuddyCard",
            "androidx.compose.material3.ScaffoldKt.Scaffold" to "QRBuddyScaffold",
//            "androidx.compose.material3.TextFieldKt.TextField" to "QRBuddyTextField",
            "androidx.compose.material3.TopAppBarKt.TopAppBar" to "QRBuddyTopAppBar",
//            "androidx.compose.material3.TextKt.Text" to "QRBuddyText",
            "androidx.compose.material3.CheckboxKt.Checkbox" to "QRBuddyCheckbox",
            "androidx.compose.material3.IconKt.Icon" to "QRBuddyIcon",
        )
    }
}