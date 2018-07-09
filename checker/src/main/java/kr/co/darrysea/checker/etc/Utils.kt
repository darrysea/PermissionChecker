package kr.co.darrysea.checker.etc

import android.Manifest
import android.app.Activity
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.util.Log
import kr.co.darrysea.checker.PermissionChecker
import kr.co.darrysea.checker.item.PermissionItem

internal object Utils {


    const val TAG: String = "PERMISSION_UTILS"
    const val PERMISSIONS = "p"
    const val MESSAGE = "m"
    const val DEBUG = "d"
    const val PERMISSION_REQUEST_CODE: Int = 1290
    const val SETTING_REQUEST_CODE: Int = 5032
    const val PREFIX_PACKAGE: String = "package:"
    var debugMode: Boolean = false

    var mPermissionListener: PermissionChecker.PermissionListener? = null

    fun debugLog(message: String?) {
        if (debugMode) {
            message?.let {
                Log.d(TAG, it)
            }
        }
    }

    fun deniedCheck(activity: Activity, permission: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }


    fun getPermission(permission: PermissionItem): String {
        return when (permission) {
            PermissionItem.READ_CALENDAR -> Manifest.permission.READ_CALENDAR
            PermissionItem.WRITE_CALENDAR -> Manifest.permission.WRITE_CALENDAR
            PermissionItem.CAMERA -> Manifest.permission.CAMERA
            PermissionItem.READ_CONTACTS -> Manifest.permission.READ_CONTACTS
            PermissionItem.WRITE_CONTACTS -> Manifest.permission.WRITE_CONTACTS
            PermissionItem.GET_ACCOUNTS -> Manifest.permission.GET_ACCOUNTS
            PermissionItem.ACCESS_FINE_LOCATION -> Manifest.permission.ACCESS_FINE_LOCATION
            PermissionItem.ACCESS_COARSE_LOCATION -> Manifest.permission.ACCESS_COARSE_LOCATION
            PermissionItem.RECORD_AUDIO -> Manifest.permission.RECORD_AUDIO
            PermissionItem.READ_PHONE_STATE -> Manifest.permission.READ_PHONE_STATE
            PermissionItem.CALL_PHONE -> Manifest.permission.CALL_PHONE
            PermissionItem.READ_CALL_LOG -> Manifest.permission.READ_CALL_LOG
            PermissionItem.WRITE_CALL_LOG -> Manifest.permission.WRITE_CALL_LOG
            PermissionItem.ADD_VOICEMAIL -> Manifest.permission.ADD_VOICEMAIL
            PermissionItem.USE_SIP -> Manifest.permission.USE_SIP
            PermissionItem.PROCESS_OUTGOING_CALLS -> Manifest.permission.PROCESS_OUTGOING_CALLS
            PermissionItem.BODY_SENSORS -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                Manifest.permission.BODY_SENSORS
            } else {
                "VERSION.SDK_INT < KITKAT_WATCH"
            }
            PermissionItem.SEND_SMS -> Manifest.permission.SEND_SMS
            PermissionItem.READ_SMS -> Manifest.permission.READ_SMS
            PermissionItem.RECEIVE_SMS -> Manifest.permission.RECEIVE_SMS
            PermissionItem.RECEIVE_WAP_PUSH -> Manifest.permission.RECEIVE_WAP_PUSH
            PermissionItem.RECEIVE_MMS -> Manifest.permission.RECEIVE_MMS
            PermissionItem.READ_EXTERNAL_STORAGE -> Manifest.permission.READ_EXTERNAL_STORAGE
            PermissionItem.WRITE_EXTERNAL_STORAGE -> Manifest.permission.WRITE_EXTERNAL_STORAGE
        }
    }
}