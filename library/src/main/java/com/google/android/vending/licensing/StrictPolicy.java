/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package com.google.android.vending.licensing;

/**
 * Non-caching policy. All requests will be sent to the licensing service,
 * and no local caching is performed.
 * <p>
 * Using a non-caching policy ensures that there is no local preference data
 * for malicious users to tamper with. As a side effect, applications
 * will not be permitted to run while offline. Developers should carefully
 * weigh the risks of using this Policy over one which implements caching,
 * such as ServerManagedPolicy.
 * <p>
 * Access to the application is only allowed if a LICESNED response is.
 * received. All other responses (including RETRY) will deny access.
 */
public class StrictPolicy implements Policy {

    private int mLastResponse;

    public StrictPolicy() {
        // Set default policy. This will force the application to check the policy on launch.
        mLastResponse = Policy.RETRY;
    }

    /**
     * Process a new response from the license server. Since we aren't
     * performing any caching, this equates to reading the LicenseResponse.
     * Any ResponseData provided is ignored.
     *
     * @param response the result from validating the server response
     * @param rawData  the raw server response data
     */
    public void processServerResponse(int response, ResponseData rawData) {
        mLastResponse = response;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation allows access if and only if a LICENSED response
     * was received the last time the server was contacted.
     */
    public boolean allowAccess() {
        return (mLastResponse == Policy.LICENSED);
    }

}
