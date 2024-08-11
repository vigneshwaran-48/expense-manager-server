package com.vapps.expense.common.util;

public class Endpoints {

    // BASE URLs
    public static final String USER_API = "/api/user";
    public static final String FAMILY_API = "/api/family";
    public static final String INVITATION_API = "/api/invitation";
    public static final String STATIC_RESOURCE_API = "/api/static";
    public static final String CATEGORY_API = "/api/category";

    // User APIs
    public static final String GET_USER_PATH = "/{userId}";
    public static final String UPDATE_USER_PATH = "/{userId}";
    public static final String GET_PROFILE_PATH = "/profile";

    public static final String CREATE_USER = USER_API;
    public static final String GET_USER = USER_API + GET_USER_PATH;
    public static final String UPDATE_USER = USER_API + UPDATE_USER_PATH;
    public static final String GET_PROFILE = USER_API + GET_PROFILE_PATH;

    // Family APIs
    public static final String UPDATE_FAMILY_PATH = "/{familyId}";
    public static final String GET_FAMILY_PATH = "/{familyId}";
    public static final String DELETE_FAMILY_PATH = "/{familyId}";
    public static final String INVITE_MEMBER_PATH = "/{familyId}/member/{memberId}/invite";
    public static final String REMOVE_MEMBER_FROM_FAMILY_PATH = "/{familyId}/member/{memberId}";
    public static final String GET_USER_FAMILY_PATH = "/user";
    public static final String SEARCH_FAMILY_PATH = "/search";

    public static final String CREATE_FAMILY = FAMILY_API;
    public static final String UPDATE_FAMILY = FAMILY_API + UPDATE_FAMILY_PATH;
    public static final String GET_FAMILY = FAMILY_API + GET_FAMILY_PATH;
    public static final String DELETE_FAMILY = FAMILY_API + DELETE_FAMILY_PATH;
    public static final String INVITE_MEMBER = FAMILY_API + INVITE_MEMBER_PATH;
    public static final String REMOVE_MEMBER_FROM_FAMILY = FAMILY_API + REMOVE_MEMBER_FROM_FAMILY_PATH;
    public static final String GET_USER_FAMILY = FAMILY_API + GET_USER_FAMILY_PATH;
    public static final String SEARCH_FAMILY = FAMILY_API + SEARCH_FAMILY_PATH;

    // Invitation APIs
    public static final String ACCEPT_INVITATION_PATH = "/{id}/accept";

    public static final String ACCEPT_INVITATION = INVITATION_API + ACCEPT_INVITATION_PATH;
    public static final String GET_ALL_INVITATIONS = INVITATION_API;

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
}