package com.android.hsdemo.network

import com.android.baselib.utils.MD5Utils
import com.android.hsdemo.model.Meeting
import com.android.hsdemo.model.MeetingDetail
import com.android.hsdemo.model.User
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import rxhttp.RxHttp

interface HttpCallback<T> {
    fun success(t: T)
    fun failed(msg: String)
}

interface RemoteRepository {

    fun login(
        userName: String,
        password: String
    ): Observable<User>

    fun getVerify(
        userName: String,
        type: Int
    ): Observable<String>

    fun modifyPassword(
        userName: String,
        authcode: String,
        password: String,
        confirmPassword: String
    ): Observable<String>

    fun register(
        userName: String,
        authcode: String,
        password: String,
        confirmPassword: String
    ): Observable<User>

    fun modifyUserInfo(
        url: String?,
        nickName: String
    ): Observable<String>

    fun getUserList(): Observable<List<User>>

    fun createMeeting(
        title: String,
        pwd: String?,
        memberIds: String?
    ): Observable<Meeting>

    fun opReport(
        id: String,
        t: Int,
        a: String?,
        e: String?
    ): Observable<String>

    fun getMeetingDetail(
        meetingNo: String
    ): Observable<MeetingDetail>

    fun joinOrLeaveMeeting(
        meetingId: String,
        type: Int
    ): Observable<String>

    fun getMyMeetingList(): Observable<List<Meeting>>

    fun getUserListByAccids(accids: String): Observable<List<User>>
}

object RemoteRepositoryImpl : RemoteRepository {

    /**
     * 登录
     */
    override fun login(userName: String, password: String): Observable<User> {
        return RxHttp.postForm(API_LOGIN)
            .add("userName", userName)
            .add("password", MD5Utils.toMD5(password))
            .asResponse(User().javaClass)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 获取验证码
     * type  场景：1-注册；2-找回密码
     */
    override fun getVerify(userName: String, type: Int): Observable<String> {
        return RxHttp.postForm(API_VERIFY)
            .add("userName", userName)
            .add("type", type)
            .asResponse(String().javaClass)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 忘记密码
     */
    override fun modifyPassword(
        userName: String,
        authcode: String,
        password: String,
        confirmPassword: String
    ): Observable<String> {
        return RxHttp.postForm(API_MODIFYPASSWORD)
            .add("userName", userName)
            .add("authcode", authcode)
            .add("password", MD5Utils.toMD5(password))
            .add("confirmPassword", MD5Utils.toMD5(confirmPassword))
            .asResponse(String().javaClass)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 注册
     */
    override fun register(
        userName: String,
        authcode: String,
        password: String,
        confirmPassword: String
    ): Observable<User> {
        return RxHttp.postForm(API_REGISTER)
            .add("userName", userName)
            .add("authcode", authcode)
            .add("password", MD5Utils.toMD5(password))
            .add("confirmPassword", MD5Utils.toMD5(confirmPassword))
            .asResponse(User().javaClass)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 修改用户信息
     * url          头像
     * nickName     昵称
     */
    override fun modifyUserInfo(url: String?, nickName: String): Observable<String> {
        return RxHttp.postForm(API_MODIFYUSERINFO)
            .add("url", url)
            .add("nickName", nickName)
            .asResponse(String().javaClass)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 用户列表接口
     */
    override fun getUserList(): Observable<List<User>> {
        return RxHttp.postForm(API_USERLIST)
            .asResponseList(User().javaClass)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 创建会议
     */
    override fun createMeeting(
        title: String,
        pwd: String?,
        memberIds: String?
    ): Observable<Meeting> {
        return RxHttp.postForm(API_CREATE_METTING)
            .add("title", title)
            .add("pwd", MD5Utils.toMD5(pwd))
            .add("memberIds", memberIds)
            .asResponse(Meeting().javaClass)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 会控上报
     * id   会议id
     * t    操作类型：1-（主持人）全员静音，2-（主持人）取消全员静音，3-（主持人）设置他人静音，4-（主持人）取消他人静音，5-设置自己静音，6-取消自己静音，7-（主持人）踢人，8-结束会议
     * a    被操控的会议成员的IM账号(type为1,2,3,4,7时必填)
     * e    扩展字段
     */
    override fun opReport(id: String, t: Int, a: String?, e: String?): Observable<String> {
        return RxHttp.postForm(API_OPREPORT_METTING)
            .add("id", id)
            .add("t", t)
            .add("a", a)
            .add("e", e)
            .asResponse(String().javaClass)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 获取会议详情
     */
    override fun getMeetingDetail(meetingNo: String): Observable<MeetingDetail> {
        return RxHttp.postForm(API_GETINFO_BY_MEETINGNO)
            .add("meetingNo", meetingNo)
            .asResponse(MeetingDetail().javaClass)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 加入/离开会议
     * type 1-加入会议，2-离开会议
     */
    override fun joinOrLeaveMeeting(meetingId: String, type: Int): Observable<String> {
        return RxHttp.postForm(API_JOIN_LEAVE_MEETING)
            .add("meetingId", meetingId)
            .add("type", type)
            .asResponse(String().javaClass)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 我的会议列表
     */
    override fun getMyMeetingList(): Observable<List<Meeting>> {
        return RxHttp.postForm(API_MYMEETINGLIST)
            .asResponseList(Meeting().javaClass)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 根据accids获取对应用户们的信息
     */
    override fun getUserListByAccids(accids: String): Observable<List<User>> {
        return RxHttp.postForm(API_USERLIST_BY_ACCIDS)
            .add("accids", accids)
            .asResponseList(User().javaClass)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

}