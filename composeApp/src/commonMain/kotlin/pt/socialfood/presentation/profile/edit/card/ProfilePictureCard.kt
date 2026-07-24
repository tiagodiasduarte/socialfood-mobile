package pt.socialfood.presentation.profile.edit.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.remember
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.edit_profile_picture_change_button
import socialfood.composeapp.generated.resources.edit_profile_picture_section_title
import socialfood.composeapp.generated.resources.edit_profile_picture_upload_label
import pt.socialfood.presentation.components.UserImage
import pt.socialfood.presentation.components.card.SectionCard
import pt.socialfood.presentation.image_picker.rememberImagePickerLauncher
import pt.socialfood.presentation.image_picker.toImageBitmap
import pt.socialfood.presentation.profile.edit.EditProfileUiState
import pt.socialfood.ui.theme.SpaceSize

@Composable
fun ProfilePictureCard(
    state: EditProfileUiState.Loaded,
    onPhotoSelected: (ByteArray, String) -> Unit,
) {
    val pickImage = rememberImagePickerLauncher(onResult = onPhotoSelected)

    SectionCard {
        Text(
            text = stringResource(Res.string.edit_profile_picture_section_title),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(Modifier.height(SpaceSize.large))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpaceSize.large),
        ) {
            Box {
                if (state.pendingImage != null) {
                    val bitmap = remember(state.pendingImage.first) { state.pendingImage.first.toImageBitmap() }
                    Image(
                        bitmap = bitmap,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape),
                    )
                } else {
                    UserImage(
                        imageUrl = state.imageUrl,
                        name = state.name,
                        imageSize = 72.dp,
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center,
                ) {
                    if (state.isUploadingPhoto) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            strokeWidth = 2.dp,
                            color = Color.White,
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.CameraAlt,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp),
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(SpaceSize.small)) {
                Text(
                    text = stringResource(Res.string.edit_profile_picture_upload_label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TextButton(
                    onClick = { if (!state.isSaving) pickImage() },
                    contentPadding = PaddingValues(0.dp),
                    enabled = !state.isSaving,
                ) {
                    Text(
                        text = stringResource(Res.string.edit_profile_picture_change_button),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}