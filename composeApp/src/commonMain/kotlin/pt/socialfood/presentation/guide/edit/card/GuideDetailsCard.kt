package pt.socialfood.presentation.guide.edit.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.presentation.components.card.SectionCard
import pt.socialfood.presentation.imagepicker.toImageBitmap
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.edit_guide_details_cover_image_click_label
import socialfood.composeapp.generated.resources.edit_guide_details_cover_image_hint
import socialfood.composeapp.generated.resources.edit_guide_details_cover_image_label
import socialfood.composeapp.generated.resources.edit_guide_details_description_error
import socialfood.composeapp.generated.resources.edit_guide_details_description_label
import socialfood.composeapp.generated.resources.edit_guide_details_description_placeholder
import socialfood.composeapp.generated.resources.edit_guide_details_section_details
import socialfood.composeapp.generated.resources.edit_guide_details_title_error
import socialfood.composeapp.generated.resources.edit_guide_details_title_label
import socialfood.composeapp.generated.resources.edit_guide_details_title_placeholder

@Composable
fun GuideDetailsCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    titleError: Boolean,
    descriptionError: Boolean,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    imageUrl: String? = null,
    isUploadingPhoto: Boolean = false,
    onPickImage: () -> Unit = {},
    pendingImage: Pair<ByteArray, String>? = null,
) {
    val focusManager = LocalFocusManager.current

    SectionCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(SpaceSize.large),
        ) {
            Text(
                text = stringResource(Res.string.edit_guide_details_section_details),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Column(verticalArrangement = Arrangement.spacedBy(SpaceSize.medium)) {
                Text(
                    text = stringResource(Res.string.edit_guide_details_cover_image_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                CoverImagePicker(
                    image = imageUrl,
                    pendingImage = pendingImage,
                    isUploadingPhoto = isUploadingPhoto,
                    onPickImage = onPickImage,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(SpaceSize.medium)) {
                Text(
                    text = stringResource(Res.string.edit_guide_details_title_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (titleError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    placeholder = {
                        Text(
                            text = stringResource(Res.string.edit_guide_details_title_placeholder),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline,
                        )
                    },
                    isError = titleError,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) },
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
                if (titleError) {
                    Text(
                        text = stringResource(Res.string.edit_guide_details_title_error),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(SpaceSize.medium)) {
                Text(
                    text = stringResource(Res.string.edit_guide_details_description_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (descriptionError) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onBackground
                    },
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    placeholder = {
                        Text(
                            text = stringResource(Res.string.edit_guide_details_description_placeholder),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline,
                        )
                    },
                    isError = descriptionError,
                    minLines = 4,
                    shape = RoundedCornerShape(12.dp),
                    colors =
                    OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
                if (descriptionError) {
                    Text(
                        text = stringResource(Res.string.edit_guide_details_description_error),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}

@Composable
private fun CoverImagePicker(
    image: String?,
    pendingImage: Pair<ByteArray, String>?,
    isUploadingPhoto: Boolean,
    onPickImage: () -> Unit,
) {
    val hasContent = pendingImage != null || image != null
    val dashedBorderColor = MaterialTheme.colorScheme.outlineVariant

    Box(
        modifier =
        Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .then(
                if (!hasContent) {
                    Modifier.drawBehind {
                        drawRoundRect(
                            color = dashedBorderColor,
                            style =
                            Stroke(
                                width = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 6f)),
                            ),
                            cornerRadius = CornerRadius(12.dp.toPx()),
                        )
                    }
                } else {
                    Modifier
                },
            ).clip(RoundedCornerShape(12.dp))
            .background(
                if (hasContent) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    MaterialTheme.colorScheme.surface
                },
            ).clickable(enabled = !isUploadingPhoto, onClick = onPickImage),
        contentAlignment = Alignment.Center,
    ) {
        when {
            pendingImage != null -> {
                val bitmap = remember(pendingImage.first) { pendingImage.first.toImageBitmap() }
                PendingCoverImage(bitmap)
            }

            image != null -> {
                AsyncImage(
                    model = image,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            else -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(SpaceSize.small),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(40.dp),
                    )
                    Text(
                        text = stringResource(Res.string.edit_guide_details_cover_image_click_label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = stringResource(Res.string.edit_guide_details_cover_image_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        if (isUploadingPhoto) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    strokeWidth = 2.dp,
                    color = Color.White,
                )
            }
        } else if (image != null || pendingImage != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(SpaceSize.medium)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Change photo",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
private fun PendingCoverImage(bitmap: ImageBitmap) {
    Image(
        bitmap = bitmap,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
    )
}

@Preview
@Composable
fun GuideDetailsCardPreview() {
    AppTheme {
        GuideDetailsCard(
            title = "Best Spots in Lisbon",
            description = "A curated list of the finest restaurants and hidden gems in Lisbon.",
            titleError = false,
            descriptionError = false,
            onTitleChange = {},
            onDescriptionChange = {},
        )
    }
}

@Preview
@Composable
fun GuideDetailsCardErrorPreview() {
    AppTheme {
        GuideDetailsCard(
            title = "",
            description = "",
            titleError = true,
            descriptionError = true,
            onTitleChange = {},
            onDescriptionChange = {},
        )
    }
}
