package com.kevin.shared.ui.overlay

import org.junit.Assert.assertEquals
import org.junit.Test

class PermissionGateTest {

    @Test
    fun granted_winsOverEverything() {
        assertEquals(PermissionGateMode.Granted, permissionGateMode(granted = true, shouldShowRationale = true, requestedOnce = true))
        assertEquals(PermissionGateMode.Granted, permissionGateMode(granted = true, shouldShowRationale = false, requestedOnce = false))
    }

    @Test
    fun firstLaunch_requests() {
        // Before the first request shouldShowRationale is false too; requestedOnce tells the cases apart.
        assertEquals(PermissionGateMode.Request, permissionGateMode(granted = false, shouldShowRationale = false, requestedOnce = false))
    }

    @Test
    fun deniedOnce_withRationale_requestsAgain() {
        assertEquals(PermissionGateMode.Request, permissionGateMode(granted = false, shouldShowRationale = true, requestedOnce = true))
    }

    @Test
    fun permanentlyDenied_opensSettings() {
        assertEquals(PermissionGateMode.OpenSettings, permissionGateMode(granted = false, shouldShowRationale = false, requestedOnce = true))
    }
}
