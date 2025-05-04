package com.getir.bootcamp.exception;

public class ExceptionMessages {
    public static final String USER_NOT_FOUND = "user_not_found";
    public static final String BOOK_NOT_FOUND = "book_not_found";
    public static final String AN_UNEXPECTED_ERROR_OCCURRED = "an_unexpected_error_occurred";
    public static final String USERNAME_ALREADY_EXISTS = "username_already_exists";
    public static final String INVALID_REFRESH_TOKEN = "invalid_refresh_token";
    public static final String REFRESH_TOKEN_IS_MISSING = "refresh_token_is_missing";
    public static final String FIELD_MUST_NOT_BE_BLANK = "field_must_not_be_blank";
    public static final String FIELD_MUST_BE_AT_MOST_100_CHARACTERS = "field_must_be_at_most_100_characters";
    public static final String FIELD_MUST_BE_AT_MOST_255_CHARACTERS = "field_must_be_at_most_255_characters";
    public static final String FIELD_MUST_BE_AT_MOST_13_CHARACTERS = "field_must_be_at_most_13_characters";
    public static final String FIELD_MUST_BE_BETWEEN_3_AND_50_CHARACTERS = "field_must_be_between_3_and_50_characters";
    public static final String FIELD_IS_REQUIRED = "field_is_required";
    public static final String CIRCULATION_NOT_FOUND = "circulation_not_found";
    public static final String BOOK_ALREADY_RETURNED = "book_already_returned";
    public static final String BOOK_NOT_AVAILABLE = "book_not_available";
    public static final String PASSWORD_MUST_BE_AT_LEAST_6_CHARACTERS = "password_must_be_at_least_6_characters";
    public static final String FIELD_MUST_BE_A_FUTURE_DATE = "field_must_be_a_future_date";
    public static final String DUE_DATE_MUST_BE_AFTER_BORROW_DATE = "due_date_must_be_after_borrow_date";
    public static final String UNAUTHORIZED_ACCESS = "unauthorized_access";
    public static final String FORBIDDEN_ACCESS = "forbidden_access";

    private ExceptionMessages() {
    }

}