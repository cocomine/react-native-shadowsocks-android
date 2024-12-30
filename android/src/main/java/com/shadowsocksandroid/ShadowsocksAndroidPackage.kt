package com.shadowsocksandroid

import com.facebook.react.TurboReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.model.ReactModuleInfo
import com.facebook.react.module.model.ReactModuleInfoProvider


class ShadowsocksAndroidPackage : TurboReactPackage() {

  override fun getModule(name: String, reactContext: ReactApplicationContext): NativeModule? {
    return if (name == ShadowsocksAndroidModule.NAME) {
      ShadowsocksAndroidModule(reactContext)
    } else {
      null
    }
  }

  override fun getReactModuleInfoProvider(): ReactModuleInfoProvider {
    return ReactModuleInfoProvider {
      val moduleInfos: MutableMap<String, ReactModuleInfo> = HashMap()
      val isTurboModule = BuildConfig.IS_NEW_ARCHITECTURE_ENABLED
      moduleInfos[ShadowsocksAndroidModule.NAME] = ReactModuleInfo(
        ShadowsocksAndroidModule.NAME,
        ShadowsocksAndroidModule.NAME,
        false,  // canOverrideExistingModule
        false,  // needsEagerInit
        true,  // hasConstants
        false,  // isCxxModule
        isTurboModule // isTurboModule
      )
      moduleInfos
    }
  }
}
