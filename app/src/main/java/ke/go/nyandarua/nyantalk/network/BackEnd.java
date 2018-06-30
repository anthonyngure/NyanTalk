package ke.go.nyandarua.nyantalk.network;


/**
 * Created by Anthony Ngure on 04/06/2017.
 * Email : anthonyngure25@gmail.com.
 * http://www.toshngure.co.ke
 */

public class BackEnd {

    public static final String BASE_URL = "http://41.215.138.10:10046/api/v1";
    public static final String URL = "http://41.215.138.10:10046";

    public static String avatarUrl(String relativeUrl) {
        return URL + "/" + relativeUrl;
    }

    public static final class EndPoints {
        public static final String TICKETS = "/tickets";
        public static final String TOPICS = "/topics";
        public static final String AUTH_SIGN_UP = "/auth/signUp";
        public static final String AUTH_SIGN_IN = "/auth/signIn";
        public static final String AUTH_FACEBOOK = "/auth/facebook";
        public static final String AUTH_VERIFICATION = "/auth/verification";
        public static final String APPOINTMENTS = "/appointments";
        public static final String CANCEL = "/cancel";
        public static final String ACCEPT = "/accept";
        public static final String COMPLETE = "/complete";
        public static final String REJECT = "/reject";
        public static final String CATEGORIES = "/categories";
        public static final String AUTH = "/auth";
        public static final String AUTH_RECOVER_PASSWORD = "/auth/password/recover";
        public static final String AUTH_RESET_PASSWORD = "/auth/password/reset";
        public static final String USERS = "/users";
        public static final String PORTFOLIOS = "/portfolios";
        public static final String PRICES = "/prices";
        public static final String CERTIFICATES = "/certificates";
        public static final String REVIEWS = "/reviews";
        public static final String SUBSCRIPTIONS = "/subscriptions";
        public static final String SUBSCRIPTIONS_ACTIVE = "/subscriptions/active";
        public static final String UNAVAILABLES = "/unavailables";
        public static final String AUTH_CHANGE_PASSWORD = "/auth/password/change";
        public static final String RESPONSES = "/responses";
        public static final String TICKETS_CREATE = "/tickets/create";
        public static final String SUB_COUNTIES = "/subcounties";
        public static final String AUTH_CODE = "/auth/code";
        public static final String CONTRIBUTIONS = "/contributions";
        public static final String FORUMS = "/forums";
        public static final String RATINGS = "/ratings";
    }

    public static final class Params {
        public static final String EMAIL = "email";
        public static final String PASSWORD = "password";
        public static final String PHONE = "phone";
        public static final String NAME = "name";
        public static final String FACEBOOK_ID = "facebookId";
        public static final String PICTURE_URL = "pictureUrl";
        public static final String FIRST_NAME = "firstName";
        public static final String LAST_NAME = "lastName";
        public static final String GENDER = "gender";
        public static final String CODE = "code";
        public static final String SIGN_IN_ID = "signInId";
        public static final String FILTER = "filter";
        public static final String INCLUDE = "include";
        public static final String CURRENT_PASSWORD = "currentPassword";
        public static final String NEW_PASSWORD = "newPassword";
        public static final String ALL = "ALL";
        public static final String DETAILS = "details";
        public static final String SUBJECT = "subject";
        public static final String SUB_COUNTY_ID = "subCountyId";
        public static final String WARD_ID = "wardId";
        public static final String DEPARTMENT_ID = "departmentId";
        public static final String PASSWORD_CONFIRMATION = "password_confirmation";
        public static final String SMS_NOTIFICATIONS_ENABLED = "smsNotificationsEnabled";
        public static final String NEW_PASSWORD_CONFIRMATION = "newPassword_confirmation";
        public static final String TEXT = "text";
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final String FORUM_ID = "forumId";
        public static final String STARS = "stars";
    }


    public static class Errors {
        public static final String PHONE_NOT_VERIFIED = "PHONE_NOT_VERIFIED";
    }
}
