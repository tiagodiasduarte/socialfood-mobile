package pt.socialfood.presentation.validatecode

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import pt.socialfood.presentation.components.AppImage
import pt.socialfood.presentation.error.stringResource
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.ShimmerColor
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.validate_code_button
import socialfood.composeapp.generated.resources.validate_code_resend_button
import socialfood.composeapp.generated.resources.validate_code_resend_label
import socialfood.composeapp.generated.resources.validate_code_restart_signup_label
import socialfood.composeapp.generated.resources.validate_code_subtitle_label
import socialfood.composeapp.generated.resources.validate_code_title_label

private const val CODE_LENGTH = 6

@Composable
fun ValidateCodeScreen(
    email: String,
    onValidateSuccess: () -> Unit,
    onRestartSignUp: () -> Unit = {},
    viewModel: ValidateCodeViewModel = koinViewModel(parameters = { parametersOf(email) }),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ValidateCodeContent(
        state = state,
        onValidateClick = viewModel::onValidate,
        onResendClick = viewModel::onResendCode,
        onRestartClick = viewModel::onRestartSignUp,
        onValidateSuccess = onValidateSuccess,
        onRestartSignUp = onRestartSignUp,
    )
}

@Composable
private fun ValidateCodeContent(
    state: ValidateCodeUiState,
    onValidateClick: (String) -> Unit,
    onResendClick: () -> Unit,
    onRestartClick: () -> Unit,
    onValidateSuccess: () -> Unit,
    onRestartSignUp: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .statusBarsPadding(),
    ) {
        when (state) {
            is ValidateCodeUiState.Error,
            is ValidateCodeUiState.ValidationError,
            ValidateCodeUiState.Idle,
            -> ValidateCodeFormView(
                state = state,
                onValidateClick = onValidateClick,
                onResendClick = onResendClick,
                onRestartClick = onRestartClick,
            )

            ValidateCodeUiState.Loading -> ValidateCodeLoadingView()

            ValidateCodeUiState.Success -> {
                onValidateSuccess()
                return
            }

            ValidateCodeUiState.RestartSignUp -> {
                onRestartSignUp()
                return
            }
        }
    }
}

@Composable
private fun ValidateCodeFormView(
    state: ValidateCodeUiState,
    onValidateClick: (String) -> Unit,
    onResendClick: () -> Unit,
    onRestartClick: () -> Unit,
) {
    var code by remember { mutableStateOf("") }
    val isComplete = code.length == CODE_LENGTH

    LaunchedEffect(code) {
        if (code.length == CODE_LENGTH) {
            onValidateClick(code)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = SpaceSize.large),
    ) {
        Column(Modifier.padding(top = SpaceSize.xxlarge)) {
            AppImage()

            Spacer(modifier = Modifier.height(SpaceSize.xxlarge))

            Text(
                text = stringResource(Res.string.validate_code_title_label),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(SpaceSize.medium))

            Text(
                text = stringResource(Res.string.validate_code_subtitle_label),
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(SpaceSize.xxlarge))

            OtpInput(
                value = code,
                onValueChange = { code = it },
                modifier = Modifier.fillMaxWidth(),
            )

            val errorMessage = when (state) {
                is ValidateCodeUiState.Error -> stringResource(state.errorCode.stringResource())
                is ValidateCodeUiState.ValidationError -> stringResource(state.message)
                else -> null
            }
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(SpaceSize.small))
                Text(
                    text = errorMessage,
                    color = colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Spacer(modifier = Modifier.height(SpaceSize.xlarge))

            Button(
                onClick = { onValidateClick(code) },
                enabled = isComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    disabledContainerColor = ShimmerColor,
                    contentColor = colorScheme.onPrimary,
                    disabledContentColor = colorScheme.onPrimary,
                ),
            ) {
                Text(
                    text = stringResource(Res.string.validate_code_button),
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Spacer(modifier = Modifier.height(SpaceSize.large))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(SpaceSize.small),
            ) {
                Text(
                    text = stringResource(Res.string.validate_code_resend_label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant,
                )
                Text(
                    text = stringResource(Res.string.validate_code_resend_button),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = colorScheme.primary,
                    modifier = Modifier
                        .padding(vertical = SpaceSize.small)
                        .clickable { onResendClick() },
                )
                Text(
                    text = stringResource(Res.string.validate_code_restart_signup_label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(vertical = SpaceSize.small)
                        .clickable { onRestartClick() },
                )
            }
        }
    }
}

@Composable
private fun OtpInput(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    BasicTextField(
        value = value,
        onValueChange = { new ->
            if (new.length <= CODE_LENGTH && new.all { it.isDigit() }) onValueChange(new)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        cursorBrush = SolidColor(Color.Transparent),
        decorationBox = {
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            ) {
                repeat(CODE_LENGTH) { index ->
                    OtpBox(
                        digit = value.getOrNull(index)?.toString() ?: "",
                        isCurrent = index == value.length,
                    )
                }
            }
        },
    )
}

@Composable
private fun OtpBox(digit: String, isCurrent: Boolean) {
    val colorScheme = MaterialTheme.colorScheme
    val filled = digit.isNotEmpty()

    Box(
        modifier = Modifier
            .size(48.dp)
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(12.dp),
            )
            .border(
                width = if (isCurrent || filled) 1.5.dp else 0.dp,
                color = if (filled) {
                    colorScheme.primary
                } else if (isCurrent) {
                    colorScheme.primary.copy(
                        alpha = 0.5f,
                    )
                } else {
                    Color.Transparent
                },
                shape = RoundedCornerShape(12.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = digit,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ValidateCodeLoadingView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            strokeWidth = 2.dp,
            modifier = Modifier.size(24.dp),
            color = colorScheme.primary,
        )
    }
}

@Composable
@Preview
fun ValidateCodeScreenPreview() {
    AppTheme {
        ValidateCodeContent(
            state = ValidateCodeUiState.Idle,
            onValidateClick = {},
            onResendClick = {},
            onRestartClick = {},
            onValidateSuccess = {},
            onRestartSignUp = {},
        )
    }
}
