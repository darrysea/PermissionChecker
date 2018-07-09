package kr.co.darrysea.permissionchecker

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import kr.co.darrysea.checker.PermissionChecker
import kr.co.darrysea.checker.item.PermissionItem

internal class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionCheck()
    }

    private fun permissionCheck() {
        PermissionChecker.Builder(this@MainActivity,
                object : PermissionChecker.PermissionListener {
                    override fun onGranted() {
                        Toast.makeText(this@MainActivity, "onGranted", Toast.LENGTH_SHORT).show()
                    }

                    override fun onDenied() {
                        Toast.makeText(this@MainActivity, "onDenied", Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(error: String) {
                        Toast.makeText(this@MainActivity, "onError : $error", Toast.LENGTH_SHORT).show()
                    }
                },
                PermissionItem.SEND_SMS,
                PermissionItem.CAMERA,
                PermissionItem.BODY_SENSORS)
                .debugMode(true)
                .settingDialogInfoMessage("설정창 이동 안내 메시지")
                .build()
    }
}
