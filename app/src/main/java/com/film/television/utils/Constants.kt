package com.film.television.utils

object Constants {
    const val TOKEN = "zxzh_app_token_apply"
    const val ENV_INFO = "zxzh_app_env_info"
    const val CONFIG_QUERY = "zxzh_app_config_query"
    const val GENERAL_VIDEO_INFO = "zxzh_app_video_general_info_query"
    const val HOMEPAGE_VIDEO_QUERY = "zxzh_app_homepage_video_query"
    const val PAGED_VIDEO_QUERY = "zxzh_app_video_query"
    const val VIDEO_SEARCH = "zxzh_app_video_search"
    const val SDK_REPORT_CONFIG_QUERY = "zxzh_sdk_report_config_query"

    const val GLOBAL_AD_SWITCH = "global_ad_switch"
    const val SPLASH_AD_SWITCH = "splash_ad_switch"
    const val INTERSTITIAL_AD_SWITCH = "interstitial_ad_switch"
    const val VIDEO_AD_SWITCH = "video_ad_switch"
    const val FEEDS_AD_SWITCH = "feeds_ad_switch"

    const val USER_AGREEMENT = "https://jk.njrzm.com/static/yhxy"
    const val PRIVACY_POLICY = "https://jk.njrzm.com/static/yszc"

    const val DELIMITER = "<<>>"
    const val PAGE_SIZE = 18

    var GLOBAL_AD_ENABLED = true
    var SPLASH_AD_ENABLED = true
    var INTERSTITIAL_AD_ENABLED = true
    var VIDEO_AD_ENABLED = true
    var FEEDS_AD_ENABLED = true
    var IS_IN_REVIEW = false

    const val KEY_CATEGORY = "category"
    const val KEY_VIDEO_BEAN = "video_bean"
    const val KEY_TITLE = "title"
    const val KEY_URL = "url"
    const val KEY_MODE = "mode"

    const val MODE_SET_PASSWORD = "set_password"
    const val MODE_VERIFY_PASSWORD = "verify_password"

    const val TEEN_MODE_MAX_USE_TIME: Long = 40 * 60 * 1000
    const val ACTION_FINISH_MAIN_ACTIVITY = "action_finish_main_activity"
}