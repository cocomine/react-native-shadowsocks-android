package com.shadowsocksandroid

import androidx.preference.PreferenceDataStore
import com.github.shadowsocks.aidl.IShadowsocksService
import com.github.shadowsocks.aidl.ShadowsocksConnection
import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.preference.OnPreferenceDataStoreChangeListener

class TestActivity : ShadowsocksConnection.Callback, OnPreferenceDataStoreChangeListener {
  override fun stateChanged(state: BaseService.State, profileName: String?, msg: String?) {
    println(state)
    println(profileName)
    println(msg)
  }

  override fun onServiceConnected(service: IShadowsocksService) {
    println(service)
  }

  override fun onServiceDisconnected() {
    println("Disconnected")
  }

  override fun onPreferenceDataStoreChanged(store: PreferenceDataStore, key: String) {
    println(store)
    println(key)
  }

}
