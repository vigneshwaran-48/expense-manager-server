package com.vapps.expense.common.util;

public class Endpoints {

    // BASE URLs
    public static final String USER_API = "/api/user";
    public static final String FAMILY_API = "/api/family";
    public static final String INVITATION_API = "/api/invitation";
    public static final String STATIC_RESOURCE_API = "/api/static";
    public static final String CATEGORY_API = "/api/category";
    public static final String EXPENSE_API = "/api/expense";
    public static final String STATS_API = "/api/stats";
    public static final String SETTINGS_API = "/api/settings";

    // User APIs
    public static final String GET_USER_PATH = "/{userId}";
    public static final String UPDATE_USER_PATH = "/{userId}";
    public static final String GET_PROFILE_PATH = "/profile";
    public static final String LOGOUT_PATH = "/logout";

    public static final String CREATE_USER = USER_API;
    public static final String GET_USER = USER_API + GET_USER_PATH;
    public static final String UPDATE_USER = USER_API + UPDATE_USER_PATH;
    public static final String GET_PROFILE = USER_API + GET_PROFILE_PATH;
    public static final String GET_ALL_USERS = USER_API;
    public static final String LOGOUT = USER_API + LOGOUT_PATH;

    // Family APIs
    public static final String UPDATE_FAMILY_PATH = "/{familyId}";
    public static final String GET_FAMILY_PATH = "/{familyId}";
    public static final String DELETE_FAMILY_PATH = "/{familyId}";
    public static final String INVITE_MEMBER_PATH = "/{familyId}/member/{memberId}/invite";
    public static final String REMOVE_MEMBER_FROM_FAMILY_PATH = "/{familyId}/member/{memberId}";
    public static final String GET_USER_FAMILY_PATH = "/user";
    public static final String SEARCH_FAMILY_PATH = "/search";
    public static final String GET_FAMILY_MEMBERS_PATH = "/{familyId}/member";
    public static final String UPDATE_FAMILY_MEMBER_ROLE_PATH = "/{familyId}/member/{memberId}";
    public static final String GET_FAMILY_MEMBER_PATH = "/{familyId}/member/{memberId}";
    public static final String FAMILY_JOIN_REQUEST_PATH = "/{familyId}/request";
    public static final String FAMILY_ACCEPT_JOIN_REQUEST_PATH = "/{familyId}/request/{requestId}/accept";
    public static final String FAMILY_REJECT_JOIN_REQUEST_PATH = "/{familyId}/request/{requestId}/reject";
    public static final String GET_USERS_FAMILY_ROLE_PATH = "/{familyId}/role";
    public static final String GET_FAMILY_INVITATIONS_PATH = "/{familyId}/invite";
    public static final String GET_FAMILY_SETTINGS_PATH = "/{familyId}/settings";
    public static final String UPDATE_FAMILY_ROLES_PATH = "/{familyId}/settings/roles";

    public static final String CREATE_FAMILY = FAMILY_API;
    public static final String UPDATE_FAMILY = FAMILY_API + UPDATE_FAMILY_PATH;
    public static final String GET_FAMILY = FAMILY_API + GET_FAMILY_PATH;
    public static final String DELETE_FAMILY = FAMILY_API + DELETE_FAMILY_PATH;
    public static final String INVITE_MEMBER = FAMILY_API + INVITE_MEMBER_PATH;
    public static final String REMOVE_MEMBER_FROM_FAMILY = FAMILY_API + REMOVE_MEMBER_FROM_FAMILY_PATH;
    public static final String GET_USER_FAMILY = FAMILY_API + GET_USER_FAMILY_PATH;
    public static final String SEARCH_FAMILY = FAMILY_API + SEARCH_FAMILY_PATH;
    public static final String GET_FAMILY_MEMBERS = FAMILY_API + GET_FAMILY_MEMBERS_PATH;
    public static final String UPDATE_FAMILY_MEMBER_ROLE = FAMILY_API + UPDATE_FAMILY_MEMBER_ROLE_PATH;
    public static final String GET_FAMILY_MEMBER = FAMILY_API + GET_FAMILY_MEMBER_PATH;
    public static final String FAMILY_JOIN_REQUEST = FAMILY_API + FAMILY_JOIN_REQUEST_PATH;
    public static final String FAMILY_ACCEPT_JOIN_REQUEST = FAMILY_API + FAMILY_ACCEPT_JOIN_REQUEST_PATH;
    public static final String FAMILY_REJECT_JOIN_REQUEST = FAMILY_API + FAMILY_REJECT_JOIN_REQUEST_PATH;
    public static final String FAMILY_LIST_JOIN_REQUEST = FAMILY_API + FAMILY_JOIN_REQUEST_PATH;
    public static final String GET_USERS_FAMILY_ROLE = FAMILY_API + GET_USERS_FAMILY_ROLE_PATH;
    public static final String GET_FAMILY_INVITATIONS = FAMILY_API + GET_FAMILY_INVITATIONS_PATH;
    public static final String GET_FAMILY_SETTINGS = FAMILY_API + GET_FAMILY_SETTINGS_PATH;
    public static final String UPDATE_FAMILY_ROLES = FAMILY_API + UPDATE_FAMILY_ROLES_PATH;

    // Invitation APIs
    public static final String ACCEPT_INVITATION_PATH = "/{id}/accept";
    public static final String RESEND_INVITATION_PATH = "/{id}/resend";
    public static final String REVOKE_INVITATION_PATH = "/{id}/revoke";

    public static final String ACCEPT_INVITATION = INVITATION_API + ACCEPT_INVITATION_PATH;
    public static final String GET_ALL_INVITATIONS = INVITATION_API;
    public static final String RESEND_INVITATION = INVITATION_API + RESEND_INVITATION_PATH;
    public static final String REVOKE_INVITATION = INVITATION_API + REVOKE_INVITATION_PATH;

    // Static Resource APIs
    public static final String GET_STATIC_RESOURCE_PATH = "/{resourceId}";
    public static final String DELETE_STATIC_RESOURCE_PATH = "/{resourceId}";

    public static final String CREATE_STATIC_RESOURCE = STATIC_RESOURCE_API;
    public static final String GET_STATIC_RESOURCE = STATIC_RESOURCE_API + GET_STATIC_RESOURCE_PATH;
    public static final String DELETE_STATIC_RESOURCE = STATIC_RESOURCE_API + DELETE_STATIC_RESOURCE_PATH;

    // Category APIs
    public static final String UPDATE_CATEGORY_PATH = "/{id}";
    public static final String DELETE_CATEGORY_PATH = "/{id}";
    public static final String GET_CATEGORY_PATH = "/{id}";

    public static final String CREATE_CATEGORY = CATEGORY_API;
    public static final String UPDATE_CATEGORY = CATEGORY_API + UPDATE_CATEGORY_PATH;
    public static final String DELETE_CATEGORY = CATEGORY_API + DELETE_CATEGORY_PATH;
    public static final String GET_CATEGORY = CATEGORY_API + GET_CATEGORY_PATH;
    public static final String GET_ALL_CATEGORIES = CATEGORY_API;

    // Expense APIs
    public static final String GET_EXPENSE_PATH = "/{id}";
    public static final String UPDATE_EXPENSE_PATH = "/{id}";
    public static final String DELETE_EXPENSE_PATH = "/{id}";

    public static final String CREATE_EXPENSE = EXPENSE_API;
    public static final String UPDATE_EXPENSE = EXPENSE_API + UPDATE_EXPENSE_PATH;
    public static final String GET_EXPENSE = EXPENSE_API + GET_EXPENSE_PATH;
    public static final String DELETE_EXPENSE = EXPENSE_API + DELETE_EXPENSE_PATH;
    public static final String GET_ALL_EXPENSES = EXPENSE_API;

    // Stats APIs
    public static final String GET_PERSONAL_STATS_PATH = "/personal";
    public static final String GET_FAMILY_STATS_PATH = "/family";

    public static final String GET_PERSONAL_STATS = STATS_API + GET_PERSONAL_STATS_PATH;
    public static final String GET_FAMILY_STATS = STATS_API + GET_FAMILY_STATS_PATH;
}