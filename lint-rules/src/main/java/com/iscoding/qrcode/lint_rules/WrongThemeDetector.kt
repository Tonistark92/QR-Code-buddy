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
import com.android.tools.lint.detector.api.SourceCodeScanner
import org.jetbrains.uast.UQualifiedReferenceExpression

/**
 * Lint rule that enforces the use of QRBuddyTheme instead of MaterialTheme.
 * This helps ensure consistent styling across the app.
 */
class WrongThemeDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes() = listOf(UQualifiedReferenceExpression::class.java)

    override fun createUastHandler(context: JavaContext) = WrongThemeHandler(context)

    class WrongThemeHandler(private val context: JavaContext) : UElementHandler() {
        override fun visitQualifiedReferenceExpression(node: UQualifiedReferenceExpression) {
            val themeMap = mapOf(
                "MaterialTheme.colorScheme" to "Theme.colors",
                "MaterialTheme.typography" to "Theme.typography",
                "MaterialTheme.shapes" to "Theme.shapes"
            )

            val text = node.asSourceString()
            val theme = themeMap.keys.find { text.startsWith(it) } ?: return

            val correctUsage = themeMap[theme]!!
            val property = text.substringAfter(theme)

            val fix = LintFix.create()
                .replace()
                .text(text)
                .with(correctUsage + property)
                .build()

            context.report(
                issue = ISSUE,
                scope = node,
                location = context.getLocation(node),
                message = "Avoid `$text`. Use `$correctUsage$property` instead from the :systemdesign module.",
                quickfixData = fix.autoFix()
            )
        }
    }

    companion object {
        private val IMPLEMENTATION = Implementation(
            WrongThemeDetector::class.java,
            Scope.JAVA_FILE_SCOPE
        )

        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "WrongThemeUsage",
            briefDescription = "QRBuddyTheme should be used instead of MaterialTheme.",
            explanation = """
                To maintain consistent design across the app, 
                use the QRBuddyTheme from the :systemdesign module instead of MaterialTheme.
            """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 6,
            severity = Severity.ERROR,
            implementation = IMPLEMENTATION
        )
    }
}
