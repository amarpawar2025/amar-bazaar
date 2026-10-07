package com.amar.domain;

public enum AccountStatus {

    PENDING_VERIFICATION, //  Account is Created  but not yet Verified 1 usage
    ACTIVE,               //  Account is active and in good standing
    SUSPENDED,            //  Account is temporarily  suspended ,possibly due to violation
    DEACTIVATED,          //  Account is deactivated ,user may have chosen to deactivate it
    BANNED,               //  Account is permanently   banned due to severe Violations
    CLOSED                // Account is permanently closed ,possible at user request
}

