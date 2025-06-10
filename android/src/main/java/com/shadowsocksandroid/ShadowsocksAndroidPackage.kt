package com.shadowsocksandroid

import android.app.Application
import android.content.Intent
import com.facebook.react.BaseReactPackage
import com.facebook.react.TurboReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.model.ReactModuleInfo
import com.facebook.react.module.model.ReactModuleInfoProvider
import com.github.shadowsocks.Core


class ShadowsocksAndroidPackage(private var application: Application) : BaseReactPackage() {

    init {
        // Initialize the Core component with the application context and TestActivity class
        Core.init(application, ShadowsocksAndroidModule::class)

        // Create an intent to start the InitService
        val intent = Intent(application, InitService::class.java)
        application.startService(intent)
    }

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
