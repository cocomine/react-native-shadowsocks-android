/*******************************************************************************
 *                                                                             *
 *  Copyright (C) 2017 by Max Lv <max.c.lv@gmail.com>                          *
 *  Copyright (C) 2017 by Mygod Studio <contact-shadowsocks-android@mygod.be>  *
 *                                                                             *
 *  This program is free software: you can redistribute it and/or modify       *
 *  it under the terms of the GNU General Public License as published by       *
 *  the Free Software Foundation, either version 3 of the License, or          *
 *  (at your option) any later version.                                        *
 *                                                                             *
 *  This program is distributed in the hope that it will be useful,            *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 *  GNU General Public License for more details.                               *
 *                                                                             *
 *  You should have received a copy of the GNU General Public License          *
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.       *
 *                                                                             *
 *******************************************************************************/

package com.shadowsocksandroid

import android.app.Activity
import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import com.github.shadowsocks.core.R
import com.github.shadowsocks.preference.DataStore
import com.github.shadowsocks.utils.Key
import com.github.shadowsocks.utils.StartService
import com.github.shadowsocks.utils.broadcastReceiver

/**
 * Activity to handle VPN connection requests.
 */
class VpnRequestActivity : AppCompatActivity() {
    private var receiver: BroadcastReceiver? = null

    /**
     * Called when the activity is starting. Initializes the activity.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (DataStore.serviceMode != Key.modeVpn) {
            finish()
            return
        }
        if (getSystemService<KeyguardManager>()!!.isKeyguardLocked) {
            // Register a BroadcastReceiver to listen for the user unlocking the device
            receiver = broadcastReceiver { _, _ -> connect.launch(null) }
            registerReceiver(receiver, IntentFilter(Intent.ACTION_USER_PRESENT))
        } else connect.launch(null)
    }

    /**
     * Registers an activity result launcher to start the VPN service.
     */
    private val connect = registerForActivityResult(StartService()) {
        if (it) {
            // Show a toast message if VPN permission is denied
            Toast.makeText(this, R.string.vpn_permission_denied, Toast.LENGTH_LONG).show()
            setResult(Activity.RESULT_CANCELED)
        } else setResult(Activity.RESULT_OK)
        finish()
    }

    /**
     * Called when the activity is destroyed. Cleans up resources.
     */
    override fun onDestroy() {
        super.onDestroy()
        if (receiver != null) unregisterReceiver(receiver)
    }
}
