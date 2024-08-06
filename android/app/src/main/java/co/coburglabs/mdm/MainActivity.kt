package co.coburglabs.mdm

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import co.coburglabs.mdm.ui.theme.MDMTheme

class MainActivity : ComponentActivity() {
    var handler: Handler? = null
    private val TAG = "MainActivity"
    var deviceAdminSample: ComponentName? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Runtime.getRuntime().exec("dpm set-device-owner co.coburglabs.mdm/.DeviceOwnerReceiver")
        //sleep(5000);
        var doMessage : String = "App's device owner state is unknown"

        if (savedInstanceState == null) {
            val manager = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
            doMessage = if (manager.isDeviceOwnerApp(applicationContext.packageName)) {
                "App is device owner"
            } else {
                "App is not device owner"
            }
        }
        deviceAdminSample = ComponentName(
            applicationContext.packageName,
            "co.coburglabs.mdm.DeviceOwnerReceiver"
        )

        Log.e(TAG, doMessage)
        val handlerThread = HandlerThread("MyHandlerThread")
        handlerThread.start()

        setContent {
            MDMTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Status(doMessage)
                }
            }
        }
        handler = Handler(handlerThread.looper)
        handler!!.post { check() }
    }
    private fun check() {
        Log.w(TAG, isActiveAdmin().toString())
        handler?.postDelayed(Runnable { check() }, 2000)
    }
    private fun isActiveAdmin(): Boolean? {
        var doMessage : String = "App's device owner state is unknown"

        val manager = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
        doMessage = if (manager.isDeviceOwnerApp(applicationContext.packageName)) {
            "App is device owner"
            deviceAdminSample?.let { manager.setSecurityLoggingEnabled(it,true) };
            return true;
        } else {
            "App is not device owner"
            return false;
        }
    }

}

@Composable
fun Status(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun StatusPreview() {
    MDMTheme {
        Status("TBD")
    }
}