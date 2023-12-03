package com.example.api.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val statusCode: HttpStatus,
    val errorCode: String,
    val errorMessage: String
) {
    UNEXPECTED_ERROR(HttpStatus.BAD_REQUEST, "E001", "Unexpected error occurred."),
    USER_ID_EXISTS(HttpStatus.BAD_REQUEST, "E002", "User id is exists."),
    INVALID_USER_ID(HttpStatus.BAD_REQUEST, "E003", "Invalid user id."),
    ALREADY_PARTNER(HttpStatus.BAD_REQUEST, "E004", "User is already subscribed to partner."),
    USER_NOT_FOUNDED(HttpStatus.BAD_REQUEST, "E005", "User not founded."),
    PASSWORD_NOT_MATCHED(HttpStatus.BAD_REQUEST, "E006", "Password is not matched."),
    INVALID_REST_KEY(HttpStatus.BAD_REQUEST, "E007", "Can't find any data."),
    INVALID_RESERVATION(HttpStatus.BAD_REQUEST, "E008", "Can't find reservation"),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "E009", "Can't find key as token"),
    NOT_AUTHORIZED(HttpStatus.BAD_REQUEST, "E010", "User don't have permission"),
    TIME_IS_NOT_UP(HttpStatus.BAD_REQUEST, "E011", "Reservation time is not up."),
    TIME_HAS_ENDED(HttpStatus.BAD_REQUEST, "E012", "Arrival confirmation time has ended."),
    CANCELED_RESERVATION(HttpStatus.BAD_REQUEST, "E013", "Reservation is already canceled."),
    NOT_APPROVAL_RESERVATION(HttpStatus.BAD_REQUEST, "E014", "Reservation is not approved."),
    REFUSED_RESERVATION(HttpStatus.BAD_REQUEST, "E015", "Reservation is refused."),
    MODIFY_KEY_NOT_SAME(HttpStatus.BAD_REQUEST, "E016", "Keys are not same."),
    DELETE_KEY_NOT_SAME(HttpStatus.BAD_REQUEST, "E017", "Keys are not same."),
    CANT_MODIFY_STATUS(HttpStatus.BAD_REQUEST, "E018", "Can't modify status."),
    ALREADY_CANCELED(HttpStatus.BAD_REQUEST, "E019", "Already canceled reservation."),
    ALREADY_REFUSED(HttpStatus.BAD_REQUEST, "E020", "Already refused reservation."),
    ALREADY_CONFIRMED(HttpStatus.BAD_REQUEST, "E021", "Already confirmed reservation."),
    EARLY_REQUEST(HttpStatus.BAD_REQUEST, "E022", "It's too early to make a reservation."),
    LATE_REQUEST(HttpStatus.BAD_REQUEST, "E022", "It's too late to make a reservation."),
    WRITTEN_REVIEW(HttpStatus.BAD_REQUEST, "E023", "This is a review that has already been written."),
    RESERVATION_NOT_FINISHED(HttpStatus.BAD_REQUEST, "E024", "Your reservation is not complete."),
    INVALID_REVIEW(HttpStatus.BAD_REQUEST, "E025", "Can't find review.");
}