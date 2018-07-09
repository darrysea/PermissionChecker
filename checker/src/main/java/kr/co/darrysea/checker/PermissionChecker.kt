package kr.co.darrysea.checker

import android.app.Activity
import android.content.Context
import android.content.Intent
import kr.co.darrysea.checker.etc.Utils
import kr.co.darrysea.checker.item.PermissionItem

class PermissionChecker private constructor(builder: Builder) {

    interface PermissionListener {
        fun onGranted()
        fun onDenied()
        fun onError(error: String)
    }

    // 필수
    private val mContext: Context
    private val mPermissions: Array<String?>
    private val mListener: PermissionListener

    // 선택
    private val mSettingDialogInfoMessage: String
    private val mDebugMode: Boolean

    init {
        mContext = builder.mContext
        mPermissions = builder.mPermissions
        mListener = builder.mListener
        mSettingDialogInfoMessage = builder.mSettingDialogInfoMessage
        mDebugMode = builder.mDebugMode

        startPermissionCheck()
    }

    private fun startPermissionCheck() {

        Utils.mPermissionListener = mListener

        Intent(mContext, PermissionCheckerActivity::class.java).apply {
            putExtra(Utils.PERMISSIONS, mPermissions)
            putExtra(Utils.MESSAGE, mSettingDialogInfoMessage)
            putExtra(Utils.DEBUG, mDebugMode)
            mContext.startActivity(this)
            try {
                (mContext as Activity).overridePendingTransition(0, 0)
            } catch (e: Exception) {

            }
        }
    }

    class Builder constructor(context: Context, listener: PermissionListener, vararg permission: PermissionItem) {
        // 필수
        internal val mContext: Context
        internal val mPermissions: Array<String?>
        internal val mListener: PermissionListener

        init {
            mContext = context
            mPermissions = setPermissionList(*permission)
            mListener = listener
        }

        // 선택
        internal var mSettingDialogInfoMessage: String = ""
        internal var mDebugMode: Boolean = false

        private fun setPermissionList(vararg item: PermissionItem): Array<String?> {
            val list: Array<String?> = arrayOfNulls(item.size)

            for (index in 0 until item.size) {
                list[index] = Utils.getPermission(item[index])
            }

            return list
        }


        fun settingDialogInfoMessage(message: String): Builder {
            mSettingDialogInfoMessage = message
            return this
        }

        fun debugMode(isDebugMode: Boolean): Builder {
            mDebugMode = isDebugMode
            return this
        }

        fun build(): PermissionChecker {
            return PermissionChecker(this)
        }
    }
}