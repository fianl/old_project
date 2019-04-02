package kr.zeroweb.bill.api

import io.reactivex.Observable
import io.reactivex.Observer
import retrofit2.http.*
import vdream.vd.com.vdream.data.*

/**
 * Created by i-03 on 18. 1. 10.
 */
interface VolunteerApiService {
    @GET("VolunteerPartcptnService/getVltrSearchWordList")
    fun getVolunteerApplyInfo(
            @Query("ServiceKey") key: String,
            @Query("_type") type: String,
            @Query("pageNo") page: Int,
            @Query("numOfRows") rows: Int
    ): Observable<VolunteerParticipantInfoResult>

    @GET("VolunteerPartcptnService/getVltrPartcptnItem")
    fun getVolunteerDetailInfo(
            @Query("progrmRegistNo") number: Int,
            @Query("_type") type: String
    ): Observable<VolunteerPartDetailResult>

    @GET("VolunteeEducationService/getVltrEducationList")
    fun getVolunteerEducationList(
            @Query("_type") type: String,
            @Query("pageNo") page: Int,
            @Query("numOfRows") rows: Int
    ): Observable<VolunteerEducationListResult>

    @GET("VolunteeEducationService/getVltrEducationItem")
    fun getVounteerEducationDetail(
            @Query("crclmRegistNo") number: Int,
            @Query("_type") type: String
    ): Observable<VolunteerEduDetailResult>
}