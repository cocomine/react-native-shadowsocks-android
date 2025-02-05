package com.shadowsocksandroid

import com.facebook.react.bridge.Promise
import timber.log.Timber

/**
 * A wrapper class for handling Promise instances in a React Native module.
 *
 * @param moduleName The name of the module using this PromiseWrapper.
 */
class PromiseWrapper(private val MODULE_NAME: String) {
    private var promise: Promise? = null
    private var nameOfCallInProgress: String? = null

    /**
     * Sets the current promise and the name of the call in progress.
     * If a promise is already in progress, it rejects the new promise.
     *
     * @param promise The Promise to be set.
     * @param nameOfCallInProgress The name of the call in progress.
     */
    fun setPromise(promise: Promise?, nameOfCallInProgress: String) {
        if (this.promise != null) {
            this.promise!!.reject(ASYNC_OP_IN_PROGRESS, "Cannot call $nameOfCallInProgress while another call is in progress")
            Timber.tag(MODULE_NAME).e("Cannot call $nameOfCallInProgress while another call is in progress")
            return
        }
        this.promise = promise
        this.nameOfCallInProgress = nameOfCallInProgress
    }

    /**
     * Resolves the current promise with the specified value and resets the state.
     *
     * @param value The value to resolve the promise with.
     */
    fun resolve(value: Any?) {
        if (promise != null) {
            promise!!.resolve(value)
            reset()
        } else {
            Timber.tag(MODULE_NAME).e("PromiseWrapper.resolve called with %s no promise", nameOfCallInProgress)
        }
    }

    /**
     * Rejects the current promise with the specified code and message, and resets the state.
     *
     * @param code The error code.
     * @param message The error message.
     */
    fun reject(code: String, message: String?) {
        if (promise != null) {
            promise!!.reject(code, message)
            reset()
        } else {
            Timber.tag(MODULE_NAME).e("PromiseWrapper.reject called with %s no promise", nameOfCallInProgress)
        }
    }

    /**
     * Resets the state of the PromiseWrapper, clearing the current promise and call name.
     */
    fun reset() {
        promise = null
        nameOfCallInProgress = null
    }

    companion object {
        const val ASYNC_OP_IN_PROGRESS: String = "ASYNC_OP_IN_PROGRESS"
    }
}
