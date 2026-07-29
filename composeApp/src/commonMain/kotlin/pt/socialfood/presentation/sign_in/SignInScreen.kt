package pt.socialfood.presentation.sign_in

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.app_name
import socialfood.composeapp.generated.resources.google_icon
import socialfood.composeapp.generated.resources.hide_password_content_description
import socialfood.composeapp.generated.resources.show_password_content_description
import socialfood.composeapp.generated.resources.sign_in_button
import socialfood.composeapp.generated.resources.sign_in_continue_with_google_label
import socialfood.composeapp.generated.resources.sign_in_email_label
import socialfood.composeapp.generated.resources.sign_in_email_placeholder_label
import socialfood.composeapp.generated.resources.sign_in_google_button
import socialfood.composeapp.generated.resources.sign_in_google_button_description
import socialfood.composeapp.generated.resources.sign_in_no_account_label
import socialfood.composeapp.generated.resources.sign_in_password_label
import socialfood.composeapp.generated.resources.sign_in_password_placeholder_label
import socialfood.composeapp.generated.resources.sign_in_sign_up_button
import socialfood.composeapp.generated.resources.sign_in_subtitle_label
import socialfood.composeapp.generated.resources.sign_in_title_label
import pt.socialfood.presentation.google.rememberGoogleSignInLauncher
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.SpaceSize

@Composable
fun SignInScreen(
    onSignInSuccess: () -> Unit,
    onSignUpClick: () -> Unit = {},
) {
    val viewModel: SignInViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state) {
        if (state is SignInUiState.Success) {
            onSignInSuccess()
            viewModel.resetState()
        }
    }

    val googleSignInLauncher = rememberGoogleSignInLauncher(
        onIdToken = { idToken -> viewModel.onGoogleSignIn(idToken) },
        onError = { message -> viewModel.onGoogleSignInError(message) },
    )

    SignInScreenContent(
        state = state,
        onSignInClick = viewModel::onSignIn,
        onSignUpClick = onSignUpClick,
        onGoogleSignInClick = googleSignInLauncher,
    )
}

@Composable
private fun SignInScreenContent(
    state: SignInUiState,
    onSignInClick: (email: String, password: String) -> Unit,
    onGoogleSignInClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {},
) {
    when (state) {
        is SignInUiState.Error,
        SignInUiState.Idle -> {
            SignInFormView(
                state = state,
                onSignInClick = onSignInClick,
                onSignUpClick = onSignUpClick,
                onGoogleSignInClick = onGoogleSignInClick,
            )
        }

        SignInUiState.Loading,
        SignInUiState.Success -> SignInLoadingView()
    }
}

@Composable
private fun SignInFormView(
    state: SignInUiState,
    onSignInClick: (email: String, password: String) -> Unit,
    onSignUpClick: () -> Unit,
    onGoogleSignInClick: () -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .padding(horizontal = SpaceSize.large)
    ) {
        Column(Modifier.align(Alignment.Center)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colorScheme.primary),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp),
                    )
                }

                Spacer(modifier = Modifier.width(SpaceSize.large))

                Text(
                    text = stringResource(Res.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    color = colorScheme.onBackground,
                )
            }

            Spacer(modifier = Modifier.height(SpaceSize.xxlarge))

            Text(
                text = stringResource(Res.string.sign_in_title_label),
                style = MaterialTheme.typography.headlineMedium,
                color = colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(SpaceSize.small))

            Text(
                text = stringResource(Res.string.sign_in_subtitle_label),
                style = MaterialTheme.typography.headlineSmall,
                color = colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(SpaceSize.xxlarge))

            Text(
                text = stringResource(Res.string.sign_in_email_label),
                style = MaterialTheme.typography.labelMedium,
                color = colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(SpaceSize.medium))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = {
                    Text(
                        text = stringResource(Res.string.sign_in_email_placeholder_label),
                        style = MaterialTheme.typography.labelMedium,
                        color = colorScheme.outline,
                    )
                },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = colorScheme.outline,
                        modifier = Modifier.size(20.dp),
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = colorScheme.outlineVariant,
                    focusedBorderColor = colorScheme.primary,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(SpaceSize.xlarge))

            Text(
                text = stringResource(Res.string.sign_in_password_label),
                style = MaterialTheme.typography.labelMedium,
                color = colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(SpaceSize.medium))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = {
                    Text(
                        text = stringResource(Res.string.sign_in_password_placeholder_label),
                        style = MaterialTheme.typography.labelMedium,
                        color = colorScheme.outline,
                    )
                },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = colorScheme.outline,
                        modifier = Modifier.size(20.dp),
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = stringResource(
                                if (passwordVisible) Res.string.hide_password_content_description
                                else Res.string.show_password_content_description
                            ),
                            tint = colorScheme.outline,
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = colorScheme.outlineVariant,
                    focusedBorderColor = colorScheme.primary,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            if (state is SignInUiState.Error) {
                Spacer(modifier = Modifier.height(SpaceSize.small))
                Text(
                    text = state.message ?: state.error.toString(),
                    color = colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Spacer(modifier = Modifier.height(SpaceSize.xlarge))

            Button(
                onClick = { onSignInClick(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary,
                ),
            ) {
                Text(
                    text = stringResource(Res.string.sign_in_button),
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Spacer(modifier = Modifier.height(SpaceSize.xlarge))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = colorScheme.outlineVariant
                )

                Text(
                    text = stringResource(Res.string.sign_in_continue_with_google_label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.outline,
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = colorScheme.outlineVariant
                )
            }

            Spacer(modifier = Modifier.height(SpaceSize.xlarge))

            OutlinedButton(
                onClick = onGoogleSignInClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, colorScheme.outlineVariant),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colorScheme.onBackground),
            ) {
                Image(
                    painter = painterResource(Res.drawable.google_icon),
                    contentDescription = stringResource(Res.string.sign_in_google_button_description),
                    modifier = Modifier.size(24.dp),
                )

                Spacer(modifier = Modifier.width(SpaceSize.large))

                Text(
                    text = stringResource(Res.string.sign_in_google_button),
                    style = MaterialTheme.typography.titleSmall,
                    color = colorScheme.onBackground,
                )
            }

            Spacer(modifier = Modifier.height(SpaceSize.xlarge))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSize.medium),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(Res.string.sign_in_no_account_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = colorScheme.onBackground,
                )

                Text(
                    modifier = Modifier
                        .padding(horizontal = SpaceSize.small)
                        .clickable { onSignUpClick() },
                    text = stringResource(Res.string.sign_in_sign_up_button),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun SignInLoadingView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            strokeWidth = 2.dp,
            modifier = Modifier.size(24.dp),
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
@Preview
fun SignInScreenPreview() {
    AppTheme {
        SignInScreenContent(
            state = SignInUiState.Idle,
            onSignInClick = { _, _ -> },
            onGoogleSignInClick = {},
        )
    }
}
