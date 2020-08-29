package com.android.hsdemo

import com.android.baselib.utils.Preferences
import com.android.hsdemo.model.User

// 相关key值
const val KEY_ROOM_ID = "room_id"
const val KEY_USER_ID = "user_id"
const val KEY_ROOM_NAME = "room_name"

const val KEY_USER_NAME = "user_name"
const val KEY_USER_NICK_NAME = "user_nick_name"
const val KEY_USER_PASSWORD = "user_password"
const val KEY_USER_BALANCE = "key_user_balance"
const val KEY_TOKEN = "token"
const val KEY_USERSIG = "user_sig"
const val KEY_ACCID = "user_accid"
const val KEY_AVATAR = "user_head_img"
const val KEY_MEETINGNOTICEACCOUNT = "meeting_notice_account"

/**
 * 事件Key
 */
object EventKey {
    const val MEETING_USER_VOICE_VOLUME = "MEETING_USER_VOICE_VOLUME"
    const val MEETING_USER_CHANGE = "MEETING_USER_CHANGE"
    const val VIDEO_CONTROL = "VIDEO_CONTROL"
    const val MEETING_STATUS_END = "MEETING_STATUS_END"
    const val LOGOUT = "LOGOUT"
}

/**
 * 会控相关
 */
object MeetingCmd {

    /**
     * 主持人全员静音
     */
    const val AUDIO_MUTE_ALL = 1

    /**
     * 主持人取消全员静音
     */
    const val AUDIO_MUTE_ALL_CANCLE = 2

    /**
     * 主持人设置他人静音
     */
    const val SET_AUDIO_MUTE_OTHERS = 3

    /**
     * 主持人取消他人静音
     */
    const val SET_AUDIO_MUTE_OTHERS_CANCLE = 4

    /**
     * 自己静音
     */
    const val SET_AUDIO_MUTE_SELF = 5

    /**
     * 自己取消静音
     */
    const val SET_AUDIO_MUTE_SELF_CANCLE = 6

    /**
     * 主持人踢人
     */
    const val KICK_OTHERS = 7

    /**
     * 结束会议
     */
    const val DESTROY_MEETING = 8

    /**
     * 自己摄像头打开
     */
    const val CAMERA_OPEN = 9

    /**
     * 自己摄像头关闭
     */
    const val CAMERA_CLOSE = 10

}


/**
 * 存储用户信息
 */
fun saveUser(t: User) {
    Preferences.saveValue(KEY_USER_NAME, t.userName.toString())
    Preferences.saveValue(KEY_USER_NICK_NAME, t.nickName.toString())
    Preferences.saveValue(KEY_USER_BALANCE, t.account.toString())
    Preferences.saveValue(KEY_TOKEN, t.token.toString())
    Preferences.saveValue(KEY_USERSIG, t.userSig.toString())
    Preferences.saveValue(KEY_AVATAR, t.userAvatar.toString())
    Preferences.saveValue(KEY_ACCID, t.accid.toString())
    Preferences.saveValue(
        KEY_MEETINGNOTICEACCOUNT,
        t.meetingNoticeAccount.toString()
    )
}

/**
 * 清除登录信息
 */
fun clearUser() {
    Preferences.saveValue(KEY_USER_NAME, "")
    Preferences.saveValue(KEY_USER_PASSWORD, "")
    Preferences.saveValue(KEY_USER_NICK_NAME, "")
    Preferences.saveValue(KEY_USER_BALANCE, "0.0")
    Preferences.saveValue(KEY_TOKEN, "")
    Preferences.saveValue(KEY_USERSIG, "")
    Preferences.saveValue(KEY_AVATAR, "")
    Preferences.saveValue(KEY_ACCID, "")
    Preferences.saveValue(KEY_MEETINGNOTICEACCOUNT, "")
}