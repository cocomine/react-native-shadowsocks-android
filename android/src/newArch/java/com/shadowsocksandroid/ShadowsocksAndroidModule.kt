package com.shadowsocksandroid

import android.app.Activity
import android.content.Intent
import com.facebook.react.ReactActivity
import com.facebook.react.bridge.BaseActivityEventListener
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableMap
import com.facebook.react.bridge.WritableNativeArray
import com.facebook.react.bridge.WritableNativeMap
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.github.shadowsocks.Core
import com.github.shadowsocks.VpnRequestActivity
import com.github.shadowsocks.database.Profile
import com.github.shadowsocks.database.ProfileManager
import com.github.shadowsocks.plugin.PluginConfiguration
import com.github.shadowsocks.plugin.PluginOptions
import timber.log.Timber


@ReactModule(name = ShadowsocksAndroidModule.NAME)
class ShadowsocksAndroidModule(reactContext: ReactApplicationContext) :
  NativeShadowsocksAndroidModuleSpec(reactContext) {

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
  override fun importProfileUri(uri: String?): WritableArray {
    val profilesArray = WritableNativeArray();

    val profiles = Profile.findAllUrls(uri)
    for (profile in profiles) {
      ProfileManager.createProfile(profile)

      val profileMap = WritableNativeMap()
      profileMap.putDouble("id", profile.id.toDouble())
      profileMap.putString("name", profile.name)
      profileMap.putString("host", profile.host)
      profileMap.putInt("remotePort", profile.remotePort)
      profileMap.putString("password", profile.password)
      profileMap.putString("method", profile.method)
      profileMap.putString("route", profile.route)
      profileMap.putString("remoteDns", profile.remoteDns)
      profileMap.putBoolean("proxyApps", profile.proxyApps)
      profileMap.putBoolean("bypass", profile.bypass)
      profileMap.putBoolean("udpdns", profile.udpdns)
      profileMap.putBoolean("ipv6", profile.ipv6)
      profileMap.putBoolean("metered", profile.metered)
      WritableNativeArray().also {
        if (profile.individual.isNotEmpty()) {
          profile.individual.split("\n").forEach { it2 -> it.pushString(it2) }
        }
        profileMap.putArray("individual", it)
      }
      PluginConfiguration(profile.plugin ?: "").getOptions().also {
        if (it.id.isNotEmpty()) {
          profileMap.putString("plugin", it.id)
          profileMap.putString("plugin_opts", it.toString())
        } else {
          profileMap.putString("plugin", null)
          profileMap.putString("plugin_opts", null)
        }
      }

      profilesArray.pushMap(profileMap)
      Timber.tag(NAME).d("Added profile $profile")
    }

    Timber.tag(NAME).d("$profilesArray")
    return profilesArray
  }

  /**
   * Adds a profile.
   * @param shadowsocksProfile The profile to add.
   * @return The new ID of the added profile.
   */
  override fun addProfile(shadowsocksProfile: ReadableMap?): Double {
    val profile = Profile();
    with(profile) {
      name = shadowsocksProfile?.getString("name") ?: ""
      host = shadowsocksProfile?.getString("host") ?: "example.shadowsocks.org"
      remotePort = shadowsocksProfile?.getInt("remotePort") ?: 8388
      password = shadowsocksProfile?.getString("password") ?: "password"
      method = shadowsocksProfile?.getString("method") ?: "aes-256-cfb"

      route = shadowsocksProfile?.getString("route") ?: "all"
      remoteDns = shadowsocksProfile?.getString("remoteDns") ?: "dns.google"
      proxyApps = shadowsocksProfile?.getBoolean("proxyApps") ?: false
      bypass = shadowsocksProfile?.getBoolean("bypass") ?: false
      udpdns = shadowsocksProfile?.getBoolean("udpdns") ?: false
      ipv6 = shadowsocksProfile?.getBoolean("ipv6") ?: false

      metered = shadowsocksProfile?.getBoolean("metered") ?: false
      val proxyAppsList = shadowsocksProfile?.getArray("individual")?.toArrayList()?.map { it.toString() } ?: emptyList()
      individual = proxyAppsList.joinToString("\n");

      val pluginId = shadowsocksProfile?.getString("plugin")
      if (!pluginId.isNullOrEmpty()) {
        plugin = PluginOptions(pluginId, shadowsocksProfile.getString("plugin_opts")).toString(false)
      }
    }

    if (shadowsocksProfile != null) {
      Timber.tag(NAME).d("$shadowsocksProfile")
    }
    Timber.tag(NAME).i("Added profile $profile")
    return ProfileManager.createProfile(profile).id.toDouble();
  }

  /**
   * Lists all profiles and returns them as a WritableArray.
   *
   * @return A WritableArray containing all profiles.
   */
  override fun listAllProfile(): WritableArray {
    val profiles = ProfileManager.getAllProfiles()
    val profilesArray = WritableNativeArray()

    if (profiles != null) {
      for (profile in profiles) {
        val profileMap = WritableNativeMap()
        profileMap.putDouble("id", profile.id.toDouble())
        profileMap.putString("name", profile.name)
        profileMap.putString("host", profile.host)
        profileMap.putInt("remotePort", profile.remotePort)
        profileMap.putString("password", profile.password)
        profileMap.putString("method", profile.method)
        profileMap.putString("route", profile.route)
        profileMap.putString("remoteDns", profile.remoteDns)
        profileMap.putBoolean("proxyApps", profile.proxyApps)
        profileMap.putBoolean("bypass", profile.bypass)
        profileMap.putBoolean("udpdns", profile.udpdns)
        profileMap.putBoolean("ipv6", profile.ipv6)
        profileMap.putBoolean("metered", profile.metered)
        WritableNativeArray().also {
          if (profile.individual.isNotEmpty()) {
            profile.individual.split("\n").forEach { it2 -> it.pushString(it2) }
          }
          profileMap.putArray("individual", it)
        }
        PluginConfiguration(profile.plugin ?: "").getOptions().also {
          if (it.id.isNotEmpty()) {
            profileMap.putString("plugin", it.id)
            profileMap.putString("plugin_opts", it.toString())
          } else {
            profileMap.putString("plugin", null)
            profileMap.putString("plugin_opts", null)
          }
        }

        profilesArray.pushMap(profileMap)
      }
    }

    Timber.tag(NAME).d("$profilesArray")
    return profilesArray
  }

  /**
   * Deletes a profile by its ID.
   *
   * @param profileId The ID of the profile to delete.
   * @return `true` if the profile was successfully deleted, `false` otherwise.
   */
  override fun deleteProfile(profileId: Double): Boolean {
    try {
      // Attempt to delete the profile using the ProfileManager
      ProfileManager.delProfile(profileId.toLong())
      Timber.tag(NAME).i("Deleted profile $profileId")
      return true
    } catch (e: IllegalStateException) {
      // Log the exception if deletion fails
      Timber.tag(NAME).e(e)
      Timber.tag(NAME).i("Deleted failed profile $profileId")
    }
    return false
  }

  /**
   * Clears all profiles from the ProfileManager.
   */
  override fun clearProfiles() {
    ProfileManager.clear()
    Timber.tag(NAME).i("Clear all profiles")
  }

  /**
   * Retrieves a profile by its ID and returns it as a WritableMap.
   *
   * @param profileId The ID of the profile to retrieve.
   * @return A WritableMap containing the profile details, or null if the profile is not found.
   */
  override fun getProfile(profileId: Double): WritableMap? {
    val profile = ProfileManager.getProfile(profileId.toLong())
    if (profile == null) {
      Timber.tag(NAME).i("Profile $profileId not found")
      return null
    }

    val profileMap = WritableNativeMap()
    profileMap.putDouble("id", profile.id.toDouble())
    profileMap.putString("name", profile.name)
    profileMap.putString("host", profile.host)
    profileMap.putInt("remotePort", profile.remotePort)
    profileMap.putString("password", profile.password)
    profileMap.putString("method", profile.method)
    profileMap.putString("route", profile.route)
    profileMap.putString("remoteDns", profile.remoteDns)
    profileMap.putBoolean("proxyApps", profile.proxyApps)
    profileMap.putBoolean("bypass", profile.bypass)
    profileMap.putBoolean("udpdns", profile.udpdns)
    profileMap.putBoolean("ipv6", profile.ipv6)
    profileMap.putBoolean("metered", profile.metered)
    WritableNativeArray().also {
      if (profile.individual.isNotEmpty()) {
        profile.individual.split("\n").forEach { it2 -> it.pushString(it2) }
      }
      profileMap.putArray("individual", it)
    }
    PluginConfiguration(profile.plugin ?: "").getOptions().also {
      if (it.id.isNotEmpty()) {
        profileMap.putString("plugin", it.id)
        profileMap.putString("plugin_opts", it.toString())
      } else {
        profileMap.putString("plugin", null)
        profileMap.putString("plugin_opts", null)
      }
    }

    Timber.tag(NAME).d("$profileMap")
    return profileMap
  }

  /**
   * Updates a profile with the given details.
   *
   * @param profile The ReadableMap containing the profile details to update.
   * @return `true` if the profile was successfully updated, `false` otherwise.
   */
  override fun updateProfile(profile: ReadableMap?): Boolean {
    val profileId = profile?.getDouble("id") ?: return false
    val profileObj = ProfileManager.getProfile(profileId.toLong()) ?: return false

    with(profileObj) {
      name = profile.getString("name") ?: name
      host = profile.getString("host") ?: host
      remotePort = profile.getInt("remotePort")
      password = profile.getString("password") ?: password
      method = profile.getString("method") ?: method

      route = profile.getString("route") ?: route
      remoteDns = profile.getString("remoteDns") ?: remoteDns
      proxyApps = profile.getBoolean("proxyApps")
      bypass = profile.getBoolean("bypass")
      udpdns = profile.getBoolean("udpdns")
      ipv6 = profile.getBoolean("ipv6")

      metered = profile.getBoolean("metered")
      val proxyAppsList = profile.getArray("individual")?.toArrayList()?.map { it.toString() } ?: emptyList()
      individual = proxyAppsList.joinToString("\n")

      val pluginId = profile.getString("plugin")
      if (!pluginId.isNullOrEmpty()) {
        plugin = PluginOptions(pluginId, profile.getString("plugin_opts")).toString(false)
      }
    }

    try {
      // Attempt to update the profile using the ProfileManager
      ProfileManager.updateProfile(profileObj)
      Timber.tag(NAME).i("Updated profile $profileObj")
      return true
    } catch (e: IllegalStateException) {
      // Log the exception if update fails
      Timber.tag(NAME).e(e)
      Timber.tag(NAME).i("Update failed for profile $profileObj")
    }
    return false
  }

  override fun connect(promise: Promise?) {
    val activity = currentActivity

    // Check if the current activity is a ReactActivity
    if (activity == null || activity !is ReactActivity) {
      Timber.tag(NAME).e("Current activity is null or not a ReactActivity")
      promise?.reject("E_ACTIVITY_ERROR", "Current activity is null or not a ReactActivity")
      return
    }

    val intent2 = Intent(activity, VpnRequestActivity::class.java)
    activity.startActivityForResult(intent2, Activity.RESULT_OK)

    promise?.resolve(true)

    Timber.tag(NAME).d("Connect to service")
  }

  override fun disconnect() {
    Core.stopService()
    Timber.tag(NAME).d("Disconnect from service")
  }

  /**
   * Switches to the specified profile by its ID and returns the profile details as a WritableMap.
   * If the profile is not found, will auto create a new default profile and switch to it.
   *
   * @param profileId The ID of the profile to switch to.
   * @return A WritableMap containing the profile details.
   */
  override fun switchProfile(profileId: Double): WritableMap {
    Timber.tag(NAME).d("Switching to profile $profileId")
    val profile = Core.switchProfile(profileId.toLong())

    val profileMap = WritableNativeMap()
    profileMap.putDouble("id", profile.id.toDouble())
    profileMap.putString("name", profile.name)
    profileMap.putString("host", profile.host)
    profileMap.putInt("remotePort", profile.remotePort)
    profileMap.putString("password", profile.password)
    profileMap.putString("method", profile.method)
    profileMap.putString("route", profile.route)
    profileMap.putString("remoteDns", profile.remoteDns)
    profileMap.putBoolean("proxyApps", profile.proxyApps)
    profileMap.putBoolean("bypass", profile.bypass)
    profileMap.putBoolean("udpdns", profile.udpdns)
    profileMap.putBoolean("ipv6", profile.ipv6)
    profileMap.putBoolean("metered", profile.metered)
    WritableNativeArray().also {
      if (profile.individual.isNotEmpty()) {
        profile.individual.split("\n").forEach { it2 -> it.pushString(it2) }
      }
      profileMap.putArray("individual", it)
    }
    PluginConfiguration(profile.plugin ?: "").getOptions().also {
      if (it.id.isNotEmpty()) {
        profileMap.putString("plugin", it.id)
        profileMap.putString("plugin_opts", it.toString())
      } else {
        profileMap.putString("plugin", null)
        profileMap.putString("plugin_opts", null)
      }
    }

    Timber.tag(NAME).d("$profileMap")
    return profileMap
  }

  companion object {
    const val NAME = "ShadowsocksAndroid"
  }
}
