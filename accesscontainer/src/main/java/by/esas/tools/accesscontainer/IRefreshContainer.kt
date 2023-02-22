/*
 * Copyright 2021 Electronic Systems And Services Ltd.
 * SPDX-License-Identifier: Apache-2.0
 */

package by.esas.tools.accesscontainer

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import by.esas.tools.accesscontainer.entity.Token
import by.esas.tools.accesscontainer.support.IContainerCancellationCallback
import by.esas.tools.logger.BaseErrorModel

interface IRefreshContainer<M : BaseErrorModel> {
    fun setToken(token: Token)
    fun getToken(): String
    fun getRefresh(): String

    fun setCancellationCallback(callback: IContainerCancellationCallback)
    fun setActivity(activity: FragmentActivity)
    fun setForgotPasswordAction(enable: Boolean, forgotPasswordAction: () -> Unit)

    /**
     * This function can refresh token without secret key
     * Access token would be refreshed anyway
     **/
    fun refresh(onComplete: (String?) -> Unit, onError: (M) -> Unit, onCancel: () -> Unit)

    /**
     * This function would always invoke dialogs with secrets if it can, because it checks if user can access secret
     * can refresh token or not
     * @see refreshExplicitly
     **/
    fun checkAccess(refreshExplicitly: Boolean = false, response: ContainerRequest<String, M>.() -> Unit)

    /**
     * This function can save refresh token, it invokes only encryption dialogs
     * Should be used only when no secret exist, because it will override old secrets
     **/
    fun saveRefresh(refreshToken: String, response: ContainerRequest<String, M>.() -> Unit)

    /**
     * This function return secret after PIN or Biometric decryption success
     **/
    fun getSecret(response: ContainerRequest<String, M>.() -> Unit)

    fun setUserId(userId: String)

    fun onCancel()

    fun clearAccess()
    fun clear()
}