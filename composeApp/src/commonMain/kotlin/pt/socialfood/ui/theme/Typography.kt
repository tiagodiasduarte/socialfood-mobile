package pt.socialfood.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.inter_bold
import socialfood.composeapp.generated.resources.inter_medium
import socialfood.composeapp.generated.resources.inter_regular

@Composable
fun interFontFamily() = FontFamily(
    Font(Res.font.inter_bold, weight = FontWeight.Bold),
    Font(Res.font.inter_medium, weight = FontWeight.Medium),
    Font(Res.font.inter_regular, weight = FontWeight.Normal),
)

val AppTypography: Typography
    @Composable get() {
        val inter = interFontFamily()
        return Typography(
            headlineMedium = TextStyle(   // "Welcome back"
                fontSize = 24.sp,
                fontFamily = inter,
                fontWeight = FontWeight.Medium,
                lineHeight = 32.sp
            ),
            headlineSmall = TextStyle(   // "Welcome back"
                fontSize = 16.sp,
                fontFamily = inter,
                fontWeight = FontWeight.Normal,
                lineHeight = 32.sp
            ),
            titleLarge = TextStyle(       // App name
                fontSize = 30.sp,
                fontFamily = inter,
                fontWeight = FontWeight.Bold,
                lineHeight = 36.sp
            ),
            titleMedium = TextStyle(      // "Continue with Google"
                fontSize = 20.sp,
                fontFamily = inter,
                fontWeight = FontWeight.Medium,
                lineHeight = 24.sp
            ),
            titleSmall = TextStyle(      // "Continue with Google"
                fontSize = 16.sp,
                fontFamily = inter,
                fontWeight = FontWeight.Medium,
                lineHeight = 24.sp
            ),
            labelLarge = TextStyle(       // Field labels + "Sign in" button
                fontSize = 16.sp,
                fontFamily = inter,
                fontWeight = FontWeight.Medium,
                lineHeight = 24.sp
            ),
            labelMedium = TextStyle(       // "Email and Password"
                fontSize = 14.sp,
                fontFamily = inter,
                fontWeight = FontWeight.Medium,
                lineHeight = 20.sp
            ),
            bodyLarge = TextStyle(
                fontSize = 18.sp,
                fontFamily = inter,
                fontWeight = FontWeight.Medium,
                lineHeight = 28.sp
            ),
            bodyMedium = TextStyle(       // "Placeholder Or continue with"
                fontSize = 14.sp,
                fontFamily = inter,
                fontWeight = FontWeight.Normal,
                lineHeight = 20.sp
            ),
            bodySmall = TextStyle(
                fontSize = 12.sp,
                fontFamily = inter,
                fontWeight = FontWeight.Normal,
                lineHeight = 20.sp
            ),
        )
    }
