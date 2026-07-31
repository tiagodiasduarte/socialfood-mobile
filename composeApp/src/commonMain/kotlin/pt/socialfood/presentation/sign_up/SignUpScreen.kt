package pt.socialfood.presentation.sign_up

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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.app_name
import socialfood.composeapp.generated.resources.hide_password_content_description
import socialfood.composeapp.generated.resources.show_password_content_description
import socialfood.composeapp.generated.resources.sign_in_email_label
import socialfood.composeapp.generated.resources.sign_in_email_placeholder_label
import socialfood.composeapp.generated.resources.sign_in_password_label
import socialfood.composeapp.generated.resources.sign_in_password_placeholder_label
import socialfood.composeapp.generated.resources.sign_up_already_have_account_label
import socialfood.composeapp.generated.resources.sign_up_button
import socialfood.composeapp.generated.resources.sign_up_confirm_password_label
import socialfood.composeapp.generated.resources.sign_up_confirm_password_placeholder_label
import socialfood.composeapp.generated.resources.sign_up_name_label
import socialfood.composeapp.generated.resources.sign_up_name_placeholder_label
import socialfood.composeapp.generated.resources.sign_up_sign_in_label
import socialfood.composeapp.generated.resources.sign_up_subtitle_label
import socialfood.composeapp.generated.resources.sign_up_title_label

@Composable
fun SignUpScreen(
    onSignUpSuccess: (email: String) -> Unit,
    onSignInClick: () -> Unit = {},
) {
    val viewModel: SignUpViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    SignUpScreenContent(
        state = state,
        onSignUpClick = { name, email, password, confirmPassword ->
            viewModel.onSignUp(name, email, password, confirmPassword)
        },
        onSignUpSuccess = onSignUpSuccess,
        onSignInClick = onSignInClick,
    )
}

@Composable
private fun SignUpScreenContent(
    state: SignUpUiState,
    onSignUpClick: (name: String, email: String, password: String, confirmPassword: String) -> Unit,
    onSignUpSuccess: (email: String) -> Unit,
    onSignInClick: () -> Unit = {},
) {
    when (state) {
        is SignUpUiState.Error,
        SignUpUiState.Idle,
        ->
            SignUpFormView(
                state = state,
                onSignUpClick = onSignUpClick,
                onSignInClick = onSignInClick,
            )

        SignUpUiState.Loading -> SignUpLoadingView()

        is SignUpUiState.Success -> {
            onSignUpSuccess(state.email)
            return
        }
    }
}

@Composable
private fun SignUpFormView(
    state: SignUpUiState,
    onSignUpClick: (name: String, email: String, password: String, confirmPassword: String) -> Unit,
    onSignInClick: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(colorScheme.background)
                .padding(horizontal = SpaceSize.large),
    ) {
        Column(Modifier.align(Alignment.Center)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier =
                        Modifier
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
                text = stringResource(Res.string.sign_up_title_label),
                style = MaterialTheme.typography.headlineMedium,
                color = colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(SpaceSize.small))

            Text(
                text = stringResource(Res.string.sign_up_subtitle_label),
                style = MaterialTheme.typography.headlineSmall,
                color = colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(SpaceSize.xxlarge))

            Text(
                text = stringResource(Res.string.sign_up_name_label),
                style = MaterialTheme.typography.labelMedium,
                color = colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(SpaceSize.medium))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = {
                    Text(
                        text = stringResource(Res.string.sign_up_name_placeholder_label),
                        style = MaterialTheme.typography.labelMedium,
                        color = colorScheme.outline,
                    )
                },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = colorScheme.outline,
                        modifier = Modifier.size(20.dp),
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors =
                    OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = colorScheme.outlineVariant,
                        focusedBorderColor = colorScheme.primary,
                    ),
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(SpaceSize.xlarge))

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
                colors =
                    OutlinedTextFieldDefaults.colors(
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
                visualTransformation =
                    if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
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
                            imageVector =
                                if (passwordVisible) {
                                    Icons.Default.VisibilityOff
                                } else {
                                    Icons.Default.Visibility
                                },
                            contentDescription =
                                stringResource(
                                    if (passwordVisible) {
                                        Res.string.hide_password_content_description
                                    } else {
                                        Res.string.show_password_content_description
                                    },
                                ),
                            tint = colorScheme.outline,
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors =
                    OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = colorScheme.outlineVariant,
                        focusedBorderColor = colorScheme.primary,
                    ),
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(SpaceSize.xlarge))

            Text(
                text = stringResource(Res.string.sign_up_confirm_password_label),
                style = MaterialTheme.typography.labelMedium,
                color = colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(SpaceSize.medium))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = {
                    Text(
                        text = stringResource(Res.string.sign_up_confirm_password_placeholder_label),
                        style = MaterialTheme.typography.labelMedium,
                        color = colorScheme.outline,
                    )
                },
                singleLine = true,
                visualTransformation =
                    if (confirmPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
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
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector =
                                if (confirmPasswordVisible) {
                                    Icons.Default.VisibilityOff
                                } else {
                                    Icons.Default.Visibility
                                },
                            contentDescription =
                                stringResource(
                                    if (confirmPasswordVisible) {
                                        Res.string.hide_password_content_description
                                    } else {
                                        Res.string.show_password_content_description
                                    },
                                ),
                            tint = colorScheme.outline,
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors =
                    OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor =
                            if (state is SignUpUiState.Error) {
                                colorScheme.error
                            } else {
                                colorScheme.outlineVariant
                            },
                        focusedBorderColor = colorScheme.primary,
                    ),
                modifier = Modifier.fillMaxWidth(),
            )

            if (state is SignUpUiState.Error) {
                Spacer(modifier = Modifier.height(SpaceSize.small))
                Text(
                    text = state.error.toString(),
                    color = colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Spacer(modifier = Modifier.height(SpaceSize.xlarge))

            Button(
                onClick = { onSignUpClick(name, email, password, confirmPassword) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary,
                        contentColor = colorScheme.onPrimary,
                    ),
            ) {
                Text(
                    text = stringResource(Res.string.sign_up_button),
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Spacer(modifier = Modifier.height(SpaceSize.xlarge))

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(SpaceSize.medium),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(Res.string.sign_up_already_have_account_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = colorScheme.onBackground,
                )

                Text(
                    modifier =
                        Modifier
                            .padding(horizontal = SpaceSize.small)
                            .clickable { onSignInClick() },
                    text = stringResource(Res.string.sign_up_sign_in_label),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun SignUpLoadingView() {
    Box(
        modifier =
            Modifier
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
fun SignUpScreenPreview() {
    AppTheme {
        SignUpScreenContent(
            state = SignUpUiState.Idle,
            onSignUpClick = { _, _, _, _ -> },
            onSignUpSuccess = { _ -> },
            onSignInClick = {},
        )
    }
}
