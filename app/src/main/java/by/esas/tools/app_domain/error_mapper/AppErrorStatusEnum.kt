package by.esas.tools.app_domain.error_mapper

import by.esas.tools.accesscontainer.error.ErrorStatusEnum

enum class AppErrorStatusEnum {
    OK,
    NOT_SET,
    UNKNOWN_ERROR,
    CLIENT_ERROR,
    SERVER_ERROR,

    //Authorization
    UNAUTHORIZED,
    ACCESS_DENIED,
    INVALID_GRANT,

    //Net
    NET_NO_CONNECTION,
    NET_UNAVAILABLE,
    NET_TIMEOUT,
    NET_SSL_HANDSHAKE,
    NET_UNKNOWN_HOST,

    //App
    APP_UNPREDICTED_ERROR,
    APP_DATE_IN_UNEXPECTED_FORMAT,
    APP_ILLEGAL_PATTERN_CHARACTER,

    APP_NO_ACCESS_TOKEN,
    APP_CONTEXT_PARAMETERS_EMPTY,
    APP_USER_HAS_NO_SECRETS,

    //Decryption
    BIOMETRIC_UNAVAILABLE,
    SECRET_CHECK_NOT_MATCH,
    DECRYPTION_FAILED,
    APP_REFRESH_TOKEN_DECRYPTION_FAILED,
    APP_PIN_DECRYPTION_FAILED,
    APP_BIOMETRIC_DECRYPTION_FAILED,
    APP_PRIVATE_KEY_DECRYPTION_FAILED,
    APP_BIOMETRIC_KEY_DECRYPTION_FAILED,
    APP_SECRET_KEY_DECRYPTION_FAILED,

    //API
    API_REDIRECTION,
    API_USER_DOES_NOT_EXIST,
    API_USER_ALREADY_EXISTS,
    API_USER_NOT_ACTIVATED,
    API_PASSWORD_RECOVERY_FAILED,
    API_ACTIVATION_FAILED,
    API_REQUEST_ERROR,
    API_WRONG_PASSWORD,
    API_USER_LOCKED_OUT,
    API_SCOPE_ERROR,
    API_ALREADY_ACTIVATED,
    API_REACTIVATE_ERROR,
    API_REGISTER_ERROR,
    API_PHONE_TAKEN,
    API_EMAIL_NUMBER_NOT_CONFIRMED,
    API_EMAIL_TAKEN,
    API_EMAIL_INVALID,
    API_USER_NAME_TAKEN,
    API_USER_NAME_INVALID,
    API_INCORRECT_PASSWORD_FORMAT,
    API_FORBIDDEN,
    API_ADD_EMAIL_ERROR,
    API_VERIFY_EMAIL_ERROR,
    API_DELETE_EMAIL_ERROR,
    API_EMAIL_ALREADY_SET,
    API_ADD_PHONE_ERROR,
    API_VERIFY_PHONE_ERROR,
    API_DELETE_PHONE_ERROR,
    API_PHONE_NUMBER_NOT_CONFIRMED,
    API_PHONE_ALREADY_SET,
    API_CODE_INVALID_OR_EXPIRED;

    companion object {
        fun getAppErrorStatusEnum(value: String): AppErrorStatusEnum {
            return try {
                valueOf(value)
            } catch (e: IllegalArgumentException) {
                mapToAppErrorStatus(value)
            }
        }

        private fun mapToAppErrorStatus(value: String): AppErrorStatusEnum {
            return try {
                when (ErrorStatusEnum.valueOf(value)) {
                    ErrorStatusEnum.HAS_NO_SECRETS -> APP_USER_HAS_NO_SECRETS
                    ErrorStatusEnum.SECRET_CHECK_NOT_MATCH -> SECRET_CHECK_NOT_MATCH
                }
            } catch (e: IllegalArgumentException) {
                UNKNOWN_ERROR
            }
        }
    }
}