package com.shadowsocksandroid

import android.app.Activity
import android.app.Application
import android.content.Intent
import com.facebook.react.ReactActivity
import com.facebook.react.bridge.BaseActivityEventListener
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.UiThreadUtil
import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableNativeArray
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.github.shadowsocks.Core
import com.github.shadowsocks.VpnRequestActivity
import com.github.shadowsocks.aidl.IShadowsocksService
import com.github.shadowsocks.aidl.ShadowsocksConnection
import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.database.Profile
import com.github.shadowsocks.database.ProfileManager
import timber.log.Timber
import kotlin.reflect.KClass


@ReactModule(name = ShadowsocksAndroidModule.NAME)
class ShadowsocksAndroidModule(reactContext: ReactApplicationContext) :
  NativeShadowsocksAndroidModuleSpec(reactContext), ShadowsocksConnection.Callback {

  init {
    reactContext.addActivityEventListener(ActivityEventListener())
  }

  private class ActivityEventListener : BaseActivityEventListener() {
    override fun onActivityResult(
      activity: Activity,
      requestCode: Int,
      resultCode: Int,
      intent: Intent?
    ) {
      if (requestCode == Activity.RESULT_OK) {
        Timber.tag(NAME).d("Activity result OK")
      } else {
        Timber.tag(NAME).d("Activity result not OK")
      }
    }
  }

  /**
   * Sends an event to the JavaScript side.
   * @param eventName The name of the event.
   * @param params The parameters to send with the event.
   */
  private fun sendEvent(eventName: String, params: String) {
    val reactContext = reactApplicationContext

    if (!reactContext.hasActiveReactInstance()) {
      Timber.tag(NAME).e("ReactApplicationContext is not ready to send events")
      return
    }

    reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit(eventName, params)
    Timber.tag(NAME).d("Sent event $eventName with params $params")
  }

  /**
   * Returns the name of the module.
   * @return The name of the module.
   */
  override fun getName(): String {
    return NAME
  }

  /**
   * Adds profiles from the given URI.
   * @param uri The URI to find profiles.
   * @return A WritableArray containing the IDs of the added profiles.
   */
  @ReactMethod
  override fun addProfile(uri: String?): WritableArray {
    val profilesArray = WritableNativeArray();

    val profiles = Profile.findAllUrls(uri)
    for (profile in profiles) {
      ProfileManager.createProfile(profile)
      profilesArray.pushDouble(profile.id.toDouble())
      Timber.tag(NAME).d("Added profile $profile")
    }

    return profilesArray
  }

  /**
   * Deletes a profile by its ID.
   * @param profileId The ID of the profile to delete.
   */
  @ReactMethod
  override fun deleteProfile(profileId: Double) {
    ProfileManager.delProfile(profileId.toLong())
    Timber.tag(NAME).d("Delete profile $profileId")
  }

  /**
   * Clears all profiles.
   */
  @ReactMethod
  override fun clearProfiles() {
    ProfileManager.clear()
    Timber.tag(NAME).d("Clear all profiles")
  }

  /**
   * Connects to the service.
   */
  @ReactMethod
  override fun connect() {
    val activity = currentActivity

    // Check if the current activity is a ReactActivity
    if (activity == null || activity !is ReactActivity) {
      Timber.tag(NAME).e("Current activity is null or not a ReactActivity")
      return
    }

    UiThreadUtil.runOnUiThread {
      val intent = Intent(activity, VpnRequestActivity::class.java)
      activity.startActivityForResult(intent, Activity.RESULT_OK)
    };

    Timber.tag(NAME).d("Connect to service")
  }

  /**
   * Disconnects from the service.
   */
  @ReactMethod
  override fun disconnect() {
    Core.stopService()
    Timber.tag(NAME).d("Disconnect from service")
  }

  /**
   * Switches to a different profile by its ID.
   * @param profileId The ID of the profile to switch to.
   * @return The ID of the switched profile.
   */
  @ReactMethod
  override fun switchProfile(profileId: Double): Double {
    Timber.tag(NAME).d("Switching to profile $profileId")
    return Core.switchProfile(profileId.toLong()).id.toDouble()
  }

  companion object {
    const val NAME = "ShadowsocksAndroid"
  }

  override fun stateChanged(state: BaseService.State, profileName: String?, msg: String?) {
    println(state)
    println(profileName)
    println(msg)
  }

  override fun onServiceConnected(service: IShadowsocksService) {
    println(service)
  }
}