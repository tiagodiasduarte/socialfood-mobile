package pt.socialfood.presentation.error

import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.error.ErrorCode
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.error_code_already_favourited
import socialfood.composeapp.generated.resources.error_code_already_following
import socialfood.composeapp.generated.resources.error_code_author_not_found
import socialfood.composeapp.generated.resources.error_code_cannot_follow_self
import socialfood.composeapp.generated.resources.error_code_code_expired
import socialfood.composeapp.generated.resources.error_code_email_already_verified
import socialfood.composeapp.generated.resources.error_code_email_not_verified
import socialfood.composeapp.generated.resources.error_code_forbidden
import socialfood.composeapp.generated.resources.error_code_guide_not_found
import socialfood.composeapp.generated.resources.error_code_home_section_not_found
import socialfood.composeapp.generated.resources.error_code_internal_error
import socialfood.composeapp.generated.resources.error_code_invalid_code
import socialfood.composeapp.generated.resources.error_code_invalid_credentials
import socialfood.composeapp.generated.resources.error_code_invalid_google_token
import socialfood.composeapp.generated.resources.error_code_invalid_request
import socialfood.composeapp.generated.resources.error_code_invalid_social_link
import socialfood.composeapp.generated.resources.error_code_item_type_mismatch
import socialfood.composeapp.generated.resources.error_code_no_pending_verification
import socialfood.composeapp.generated.resources.error_code_not_favourited
import socialfood.composeapp.generated.resources.error_code_not_following
import socialfood.composeapp.generated.resources.error_code_resend_too_soon
import socialfood.composeapp.generated.resources.error_code_restaurant_not_found
import socialfood.composeapp.generated.resources.error_code_restaurant_not_in_guide
import socialfood.composeapp.generated.resources.error_code_unknown
import socialfood.composeapp.generated.resources.error_code_user_already_exists
import socialfood.composeapp.generated.resources.error_code_user_not_found

private val errorCodeStringResources: Map<ErrorCode, StringResource> = mapOf(
    ErrorCode.USER_ALREADY_EXISTS to Res.string.error_code_user_already_exists,
    ErrorCode.INVALID_CREDENTIALS to Res.string.error_code_invalid_credentials,
    ErrorCode.EMAIL_NOT_VERIFIED to Res.string.error_code_email_not_verified,
    ErrorCode.EMAIL_ALREADY_VERIFIED to Res.string.error_code_email_already_verified,
    ErrorCode.INVALID_CODE to Res.string.error_code_invalid_code,
    ErrorCode.CODE_EXPIRED to Res.string.error_code_code_expired,
    ErrorCode.NO_PENDING_VERIFICATION to Res.string.error_code_no_pending_verification,
    ErrorCode.RESEND_TOO_SOON to Res.string.error_code_resend_too_soon,
    ErrorCode.INVALID_GOOGLE_TOKEN to Res.string.error_code_invalid_google_token,
    ErrorCode.USER_NOT_FOUND to Res.string.error_code_user_not_found,
    ErrorCode.GUIDE_NOT_FOUND to Res.string.error_code_guide_not_found,
    ErrorCode.AUTHOR_NOT_FOUND to Res.string.error_code_author_not_found,
    ErrorCode.RESTAURANT_NOT_FOUND to Res.string.error_code_restaurant_not_found,
    ErrorCode.RESTAURANT_NOT_IN_GUIDE to Res.string.error_code_restaurant_not_in_guide,
    ErrorCode.HOME_SECTION_NOT_FOUND to Res.string.error_code_home_section_not_found,
    ErrorCode.CANNOT_FOLLOW_SELF to Res.string.error_code_cannot_follow_self,
    ErrorCode.ALREADY_FOLLOWING to Res.string.error_code_already_following,
    ErrorCode.NOT_FOLLOWING to Res.string.error_code_not_following,
    ErrorCode.ALREADY_FAVOURITED to Res.string.error_code_already_favourited,
    ErrorCode.NOT_FAVOURITED to Res.string.error_code_not_favourited,
    ErrorCode.ITEM_TYPE_MISMATCH to Res.string.error_code_item_type_mismatch,
    ErrorCode.FORBIDDEN to Res.string.error_code_forbidden,
    ErrorCode.INVALID_REQUEST to Res.string.error_code_invalid_request,
    ErrorCode.INTERNAL_ERROR to Res.string.error_code_internal_error,
    ErrorCode.INVALID_SOCIAL_LINK to Res.string.error_code_invalid_social_link,
    ErrorCode.UNKNOWN to Res.string.error_code_unknown,
)

fun ErrorCode.stringResource(): StringResource = errorCodeStringResources.getValue(this)

suspend fun DataError.displayMessage(): String = when (this) {
    is DataError.Known -> getString(errorCode.stringResource())
    is DataError.Unknown -> message ?: "Something went wrong"
    is DataError.Network -> "Something went wrong"
}
