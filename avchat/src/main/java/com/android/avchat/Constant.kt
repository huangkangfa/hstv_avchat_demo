package com.android.avchat

object Constant {
    const val ROOM_ID = "room_id"
    const val USER_ID = "user_id"
    const val ROLE_TYPE = "role_type"
    const val CUSTOM_VIDEO = "custom_video"

    // 美颜风格.三种美颜风格：0 ：光滑  1：自然  2：朦胧
    const val BEAUTY_STYLE_SMOOTH = 0
    const val BEAUTY_STYLE_NATURE = 1
    const val VIDEO_FPS = 15

    // RTC 通话场景：640*360，15fps，550kbps
    const val RTC_VIDEO_BITRATE = 550

    // 直播场景：连麦小主播：270*480, 15pfs, 400kbps
    const val LIVE_270_480_VIDEO_BITRATE = 400
    const val LIVE_360_640_VIDEO_BITRATE = 900

    // 直播场景：大主播：默认 540*960, 15fps，1200kbps
    const val LIVE_540_960_VIDEO_BITRATE = 1200
    const val LIVE_720_1280_VIDEO_BITRATE = 1500
}