package com.shadowsocksandroid

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.github.shadowsocks.Core

/**
 * A service that initializes the Core component when started.
 */
class InitService : Service() {

  /**
   * Called by the system every time a client explicitly starts the service by calling
   * Context.startService(Intent).
   *
   * @param intent The Intent supplied to Context.startService(Intent), as given.
   * @param flags Additional data about this start request.
   * @param startId A unique integer representing this specific request to start.
   * @return The return value indicates what semantics the system should use for the service's
   * current started state. It is either one of the constants associated with START_STICKY,
   * START_NOT_STICKY, or START_REDELIVER_INTENT.
   */
  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    // Initialize the Core component with the application context and TestActivity class
    Core.init(application, TestActivity::class)
    return START_STICKY
  }

  /**
   * Return the communication channel to the service. May return null if clients can not bind to the service.
   *
   * @param intent The Intent that was used to bind to this service.
   * @return Return an IBinder through which clients can call on to the service.
   */
  override fun onBind(intent: Intent?): IBinder? {
    return null
  }
}
