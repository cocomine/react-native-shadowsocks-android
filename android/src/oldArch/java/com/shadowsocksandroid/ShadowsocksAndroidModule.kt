package com.shadowsocksandroid

import android.app.Activity
import android.content.Intent
import com.facebook.react.ReactActivity
import com.facebook.react.bridge.BaseActivityEventListener
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.UiThreadUtil
import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableMap
import com.facebook.react.bridge.WritableNativeArray
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.github.shadowsocks.Core
import com.github.shadowsocks.VpnRequestActivity
import com.github.shadowsocks.aidl.IShadowsocksService
import com.github.shadowsocks.aidl.ShadowsocksConnection
import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.database.Profile
import com.github.shadowsocks.database.ProfileManager
import com.github.shadowsocks.plugin.PluginOptions
import timber.log.Timber

class ShadowsocksAndroidModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext), ShadowsocksConnection.Callback {

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
  @ReactMethod(isBlockingSynchronousMethod = true)
  fun importProfileUri(uri: String?): WritableArray {
    val profilesArray = WritableNativeArray();

    val profiles = Profile.findAllUrls(uri)
    for (profile in profiles) {
      ProfileManager.createProfile(profile)
      profilesArray.pushDouble(profile.id.toDouble())
      Timber.tag(NAME).d("Added profile $profile")
    }

    return profilesArray
    TODO("Not yet implemented")
  }

  /**
   * Adds a profile.
   * @param ShadowsocksProfile The profile to add.
   * @return The new ID of the added profile.
   */
  @ReactMethod(isBlockingSynchronousMethod = true)
  fun addProfile(ShadowsocksProfile: ReadableMap?): Double {
    val profile = Profile();
    with(profile){
      name = ShadowsocksProfile?.getString("name") ?: ""
      host = ShadowsocksProfile?.getString("host") ?: "example.shadowsocks.org"
      remotePort = ShadowsocksProfile?.getInt("remotePort") ?: 8388
      password = ShadowsocksProfile?.getString("password") ?: "password"
      method = ShadowsocksProfile?.getString("method") ?: "aes-256-cfb"

      route = ShadowsocksProfile?.getString("route") ?: "all"
      remoteDns = ShadowsocksProfile?.getString("remoteDns") ?: "dns.google"
      proxyApps = ShadowsocksProfile?.getBoolean("proxyApps") ?: false
      bypass = ShadowsocksProfile?.getBoolean("bypass") ?: false
      udpdns = ShadowsocksProfile?.getBoolean("udpdns") ?: false
      ipv6 = ShadowsocksProfile?.getBoolean("ipv6") ?: false

      metered = ShadowsocksProfile?.getBoolean("metered") ?: false
      individual = ShadowsocksProfile?.getString("individual") ?: ""

      val pluginId = ShadowsocksProfile?.getString("plugin")
      if (!pluginId.isNullOrEmpty()) {
        plugin = PluginOptions(pluginId, ShadowsocksProfile.getString("plugin_opts")).toString(false)
      }
    }

    Timber.tag(NAME).i("Added profile $profile")
    return ProfileManager.createProfile(profile).id.toDouble();
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  fun listAllProfile(): WritableArray {
    TODO("Not yet implemented")
  }

  /**
   * Deletes a profile by its ID.
   * @param profileId The ID of the profile to delete.
   */
  @ReactMethod
  fun deleteProfile(profileId: Double) {
    ProfileManager.delProfile(profileId.toLong())
    Timber.tag(NAME).d("Delete profile $profileId")
  }

  /**
   * Clears all profiles.
   */
  @ReactMethod
  fun clearProfiles() {
    ProfileManager.clear()
    Timber.tag(NAME).d("Clear all profiles")
  }

  /**
   * Connects to the service.
   */
  @ReactMethod
  fun connect(promise: Promise?) {
    val activity = currentActivity

    // Check if the current activity is a ReactActivity
    if (activity == null || activity !is ReactActivity) {
      Timber.tag(NAME).e("Current activity is null or not a ReactActivity")
      promise?.reject("E_ACTIVITY_ERROR", "Current activity is null or not a ReactActivity")
      return
    }

    UiThreadUtil.runOnUiThread {
      val intent = Intent(activity, VpnRequestActivity::class.java)
      activity.startActivityForResult(intent, Activity.RESULT_OK)
      promise?.resolve(true)
    };

    Timber.tag(NAME).d("Connect to service")
    TODO("Not yet implemented")
  }

  /**
   * Disconnects from the service.
   */
  @ReactMethod
  fun disconnect() {
    Core.stopService()
    Timber.tag(NAME).d("Disconnect from service")
  }

  /**
   * Switches to a different profile by its ID.
   * @param profileId The ID of the profile to switch to.
   * @return The ID of the switched profile.
   */
  @ReactMethod(isBlockingSynchronousMethod = true)
  fun switchProfile(profileId: Double): WritableMap? {
    Timber.tag(NAME).d("Switching to profile $profileId")
    val profile = Core.switchProfile(profileId.toLong())
    TODO("Not yet implemented")
  }

  companion object {
    const val NAME = "ShadowsocksAndroid"
  }

  override fun stateChanged(state: BaseService.State, profileName: String?, msg: String?) {
    TODO("Not yet implemented")
  }

  override fun onServiceConnected(service: IShadowsocksService) {
    TODO("Not yet implemented")
  }
}
