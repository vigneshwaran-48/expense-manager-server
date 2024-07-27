package com.vapps.expense.util;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.List;

public class TestUtil {

    public static void logTestCasePassed(String name, String description) {
        System.out.println("\n----------------------------------------------------------------------\n");
        System.out.println("Test case: " + name + " is passed");
        System.out.println("Description: " + description);
        System.out.println("\n----------------------------------------------------------------------\n");
    }

    public static OidcUser getOidcUser(String userId, List<String> scopes) {
        return new DefaultOidcUser(AuthorityUtils.createAuthorityList(scopes),
                OidcIdToken.withTokenValue("id-token").claim("sub", userId).build(), "sub");
    }
}
