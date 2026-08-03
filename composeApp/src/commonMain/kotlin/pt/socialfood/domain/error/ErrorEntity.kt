package pt.socialfood.domain.error

sealed class ErrorEntity {
    sealed class Network : ErrorEntity() {
        object ACCESS_DENIED : Network()
        object NOT_FOUND : Network()
        object TIMEOUT : Network()
        object INTERNAL_SERVER_ERROR : Network()
        object SERVER_UNAVAILABLE : Network()
        object UNKNOWN : Network()
    }

    sealed class Validation : ErrorEntity() {
        object EmptyTitle : Validation()
        object EmptyDescription : Validation()
        object PublicGuideNeedsMoreRestaurants : Validation()
        object PublicGuideNeedsImage : Validation()
    }

    data class Api(val message: String) : ErrorEntity()

    object Unauthorized : ErrorEntity()
    object InvalidCredentials : ErrorEntity()
    object PasswordMismatch : ErrorEntity()
    object Unknown : ErrorEntity()
}
