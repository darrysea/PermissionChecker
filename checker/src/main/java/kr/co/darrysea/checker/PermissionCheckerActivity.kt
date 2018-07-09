package kr.co.darrysea.checker

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.ContextThemeWrapper
import android.view.ViewGroup
import android.widget.RelativeLayout
import kr.co.darrysea.checker.etc.Utils
import kr.co.darrysea.checker.etc.Utils.DEBUG
import kr.co.darrysea.checker.etc.Utils.MESSAGE
import kr.co.darrysea.checker.etc.Utils.PERMISSIONS
import kr.co.darrysea.checker.etc.Utils.PERMISSION_REQUEST_CODE
import kr.co.darrysea.checker.etc.Utils.PREFIX_PACKAGE
import kr.co.darrysea.checker.etc.Utils.SETTING_REQUEST_CODE
import kr.co.darrysea.checker.etc.Utils.deniedCheck
import kr.co.darrysea.checker.etc.Utils.mPermissionListener

internal class PermissionCheckerActivity : Activity() {

    private val mContext = this
    private lateinit var mSettingDialogMessage: String
    private var mPermissionList: Array<String> = arrayOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(initView(), initLayoutParams())

        if (getData()) {
            checkPermission()
        } else {
            finish()
        }
    }

    private fun initView(): RelativeLayout =
            RelativeLayout(mContext)

    private fun initLayoutParams(): ViewGroup.LayoutParams =
            RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

    private fun getData(): Boolean {
        Utils.debugMode = intent.getBooleanExtra(DEBUG, false)

        (intent.getStringExtra(MESSAGE))?.let {
            mSettingDialogMessage =
                    if (it.isEmpty())
                        resources.getString(R.string.setting_dialog_message)
                    else
                        it
        } ?: run {
            mSettingDialogMessage = resources.getString(R.string.setting_dialog_message)
        }

        (intent.getStringArrayExtra(PERMISSIONS))?.let {
            if (it.isNotEmpty()) {
                mPermissionList = it
                return true
            }
        }

        mPermissionListener?.onError(resources.getString(R.string.permission_missing))
        return false
    }

    private fun checkPermission() {
        try {
            var isGranted = true
            val deniedList: ArrayList<String> = arrayListOf()

            for (permission in mPermissionList) {
                permission.let {
                    if (ContextCompat.checkSelfPermission(mContext, it) != PackageManager.PERMISSION_GRANTED) {
                        isGranted = false
                        deniedList.add(it)
                    }
                }
            }

            if (isGranted) {
                mPermissionListener?.onGranted()
                finish()
            } else {
                val list: Array<String?> = arrayOfNulls(deniedList.size)

                for (index in 0 until deniedList.size) {
                    list[index] = deniedList[index]
                }

                ActivityCompat.requestPermissions(mContext, list, PERMISSION_REQUEST_CODE)
            }

        } catch (e: Exception) {
            e.message?.let {
                mPermissionListener?.onError(it)
            } ?: run {
                mPermissionListener?.onError(mContext.resources.getString(R.string.etc_error))
            }

            finish()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            grantResults?.let {
                var isGrant = true
                var isSendingSettings = false

                for (result in it) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        isGrant = false

                        permissions?.let {
                            for (permission in it) {
                                if (!deniedCheck(mContext, permission)) {
                                    isSendingSettings = true
                                }
                            }
                        }
                    }
                }

                if (isGrant) {
                    mPermissionListener?.onGranted()
                    finish()
                } else {
                    if (isSendingSettings) {
                        sendingSettingsDialog()
                    } else {
                        mPermissionListener?.onDenied()
                        finish()
                    }
                }
            }
        }
    }


    private fun sendingSettingsDialog() {
        var resID = android.R.style.Theme_DeviceDefault_Light_Dialog

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            resID = android.R.style.Theme_Material_Light_Dialog_Alert
        }

        AlertDialog.Builder(ContextThemeWrapper(mContext, resID)).apply {
            setMessage(mSettingDialogMessage)
            setCancelable(false)
            setPositiveButton(mContext.resources.getString(R.string.setting)) { dialog, _ ->
                startActivityForResult(
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse(PREFIX_PACKAGE + packageName)),
                        SETTING_REQUEST_CODE)
                dialog.dismiss()
            }
            setNegativeButton(mContext.resources.getString(R.string.close)) { dialog, _ ->
                mPermissionListener?.onDenied()
                dialog.dismiss()

                this@PermissionCheckerActivity.finish()
            }
            show()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    override fun onDestroy() {
        mPermissionListener = null
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SETTING_REQUEST_CODE) {
            checkPermission()
        }
    }
}
