package kr.zeroweb.bill.api

import io.reactivex.Observable
import io.reactivex.Observer
import retrofit2.http.*
import vdream.vd.com.vdream.data.*

/**
 * Created by i-03 on 18. 1. 10.
 */
interface ApiService {
    @GET("/v1/version")
    fun getAppVersion(): Observable<AppVersionResult>

    @POST("/v1/sign_in")
    fun login(@Body body : SignUpData) : Observable<SignInResult>

    @GET("/v1/sign_out")
    fun logout(): Observable<BaseResultData>

    @GET("/v1/validate_username/{username}")
    fun validateUsername(
            @Path("username") usernam: String
    ): Observable<BaseResultData>

    @GET("/v1/validate_email/{email}")
    fun validateEmail(
            @Path("email") email: String
    ): Observable<BaseResultData>

    @POST("/v1/sign_up")
    fun signIn(@Body body: SignUpData) : Observable<SignInResult>

    @GET("/v1/me")
    fun getMyInfo(): Observable<UserInfoResult>

    @PUT("/v1/me")
    fun updateUserInfo(
            @Body body: UpdateUserData
    ): Observable<BaseResultData>

    @DELETE("/v1/me")
    fun deleteUserInfo() : Observable<BaseResultData>

    @GET("/v1/interest_category")
    fun getInterestCategory(): Observable<CategoriesResultData>

    @GET("/v1/interest")
    fun getMyInterests(): Observable<CategoriesResultData>

    @PUT("/v1/interest")
    fun updateMyInterests(
            @Body body: InterestUpdateData
    ): Observable<CategoriesResultData>

    @GET("/v1/user/{idx}")
    fun getOtherUserInfo(
            @Path("idx") idx: Int
    ): Observable<UserInfoData>

    @GET("/v1/validate_classroom/{classroom}")
    fun validateClassroomTitle(
            @Path("classroom") title: String
    ): Observable<BaseResultData>

    @GET("/v1/classroom_category")
    fun getFirstCategory(): Observable<CategoriesResultData>

    @GET("/v1/classroom_category/{category}")
    fun getSecondCategory(
            @Path("category") idx: Int
    ): Observable<CategoriesResultData>

    @POST("/v1/classroom")
    fun registerClass(
            @Body body: RegisterClassData
    ): Observable<RegisterResultData>

    @GET("/v1/classroom/{classroom}")
    fun getClassDetailInfo(
            @Path("classroom") idx: Int
    ): Observable<ClassDetailResult>

    @GET("/v1/classroom/{classroom}/feeds/{page}")
    fun getNextFeeds(
            @Path("classroom") classIdx: Int,
            @Path("page") page: Int
    ): Observable<FeedResultData>

    @GET("/v1/classroom/{classroom}/notices/{page}")
    fun getNextNotices(
            @Path("classroom") classIdx: Int,
            @Path("page") page: Int
    ): Observable<FeedResultData>

    @GET("/v1/classroom/{classroom}/albums/{page}")
    fun getNextAlbums(
            @Path("classroom") classIdx: Int,
            @Path("page") page: Int
    ): Observable<AlbumResult>

    @POST("/v1/classroom/{classroom}/board")
    fun registerAnnounce(
            @Path("classroom") idx: Int,
            @Body body: BoardRegisterData
    ): Observable<RegisterResultData>

    @PUT("/v1/classroom/{classroom}/board/{article}")
    fun updateAnnounce(
            @Path("classroom") idx: Int,
            @Path("article") fIdx: Int,
            @Body body: BoardRegisterData
    ): Observable<RegisterResultData>

    @DELETE("/v1/classroom/{classroom}/board/{article}")
    fun deleteAnnounce(
            @Path("classroom") classIdx: Int,
            @Path("article") articleIdx: Int
    ): Observable<BaseResultData>

    @POST("/v1/classrooms/{page}")
    fun getClassList(
            @Path("page") page: Int,
            @Body body: ClassListRequestData
    ): Observable<ClassListResultData>

    @PUT("/v1/classroom/{classroom}")
    fun updateClassroomInfo(
            @Path("classroom") idx: Int,
            @Body body: RegisterClassData
    ):Observable<BaseResultData>

    @POST("/v1//classroom/{classroom}/join")
    fun classJoin(
            @Path("classroom") idx: Int
    ): Observable<BaseResultData>

    @PUT("/v1//classroom/{classroom}/leave")
    fun classLeave(
            @Path("classroom") idx: Int
    ): Observable<BaseResultData>

    @GET("/v1/classroom/{classroom}/board/{article}/comment")
    fun getCommentList(
            @Path("classroom") classRoom: Int,
            @Path("article") article: Int
    ): Observable<CommentListResultData>

    @POST("/v1/classroom/{classroom}/board/{article}/comment")
    fun writeComment(
            @Path("classroom") classroom: Int,
            @Path("article") article: Int,
            @Body body: CommentWriteData
    ): Observable<RegisterResultData>

    @PUT("/v1/classroom/{classroom}/board/{article}/comment/{comment}")
    fun editComment(
            @Path("classroom") classroom: Int,
            @Path("article") article: Int,
            @Path("comment") comment: Int,
            @Body body: CommentWriteData
    ): Observable<RegisterResultData>

    @PUT("/v1/classroom/{classroom}/board/{article}/comment/{comment}")
    fun deleteComment(
            @Path("classroom") classroom: Int,
            @Path("article") article: Int,
            @Path("comment") comment: Int
    ): Observable<BaseResultData>

    @POST("/v1/classroom/{classroom}/subscribe")
    fun classSubscribe(
            @Path("classroom") idx: Int
    ): Observable<BaseResultData>

    @PUT("/v1/classroom/{classroom}/unsubscribe")
    fun classSubscribeCancel(
            @Path("classroom") idx: Int
    ): Observable<BaseResultData>

    @POST("/v1/classroom/{classroom}/board/{article}/like")
    fun noticeLike(
            @Path("classroom") classIdx: Int,
            @Path("article") artcleIdx: Int
    ): Observable<BaseResultData>

    @PUT("/v1/classroom/{classroom}/board/{article}/unlike")
    fun noticeLikeCancel(
            @Path("classroom") classIdx: Int,
            @Path("article") artcleIdx: Int
    ): Observable<BaseResultData>

    @POST("/v1/experience")
    fun registerExperience(
            @Body body: ExperienceRegisterData
    ): Observable<RegisterResultData>

    @PUT("/v1/experience/{article}")
    fun updateExperience(
            @Path("article") articleIdx: Int,
            @Body body: ExperienceRegisterData
    ): Observable<BaseResultData>

    @DELETE("/v1/experience/{article}")
    fun deleteExperience(
            @Path("article") articleIdx: Int
    ): Observable<BaseResultData>

    @GET("/v1/experiences/{page}")
    fun getExperiecne(
            @Path("page") page: Int
    ): Observable<FeedResultData>

    @GET("/v1/experience/{article}/comment")
    fun getExperienceCommentList(
            @Path("article") articleIdx: Int
    ): Observable<CommentListResultData>

    @POST("/v1/experience/{article}/comment")
    fun writeExperienceComment(
            @Path("article") articleIdx: Int,
            @Body body: CommentWriteData
    ): Observable<RegisterResultData>

    @PUT("/v1/experience/{article}/comment/{comment}")
    fun updateExperienceComment(
            @Path("article") articleIdx: Int,
            @Path("comment") commentIdx: Int,
            @Body body: CommentWriteData
    ): Observable<BaseResultData>

    @DELETE("/v1/experience/{article}/comment/{comment}")
    fun deleteExperienceComment(
            @Path("article") articleIdx: Int,
            @Path("comment") commentIdx: Int
    )

    @POST("/v1/experience/{article}/like")
    fun experienceLike(
            @Path("article") articleIdx: Int
    ): Observable<BaseResultData>

    @PUT("/v1/experience/{article}/unlike")
    fun experienceLikeCancel(
            @Path("article") articleIdx: Int
    ): Observable<BaseResultData>

    @POST("/v1/diary")
    fun registerRecord(
            @Body body: RecordRegisterData
    ): Observable<RegisterResultData>

    @PUT("/v1/diary/{article}")
    fun updateRecord(
            @Path("article") idx: Int,
            @Body body: RecordRegisterData
    ): Observable<BaseResultData>

    @DELETE("/v1/diary/{article}")
    fun deleteRecord(
            @Path("article") idx: Int
    ): Observable<BaseResultData>

    @GET("/v1/diary/{user}/record/{kind}/{page}")
    fun getUserRecord(
            @Path("user") uuid: String,
            @Path("kind") kind: String,
            @Path("page") page: Int
    ): Observable<RecordListResultData>

    @GET("/v1/diary/{user}/note/{page}")
    fun getUserDiary(
            @Path("user") uuid: String,
            @Path("page") page: Int
    ): Observable<DiaryListResultData>

    @GET("/v1/diary/{article}/comment")
    fun getRecordCommentList(
            @Path("article") idx: Int
    ): Observable<CommentListResultData>

    @POST("/v1/diary/{article}/comment")
    fun registerRecordComment(
            @Path("article") idx: Int,
            @Body body: CommentWriteData
    ): Observable<RegisterResultData>

    @PUT("/v1/diary/{article}/comment/{comment}")
    fun updateRecordComment(
            @Path("article") idx: Int,
            @Path("comment") commentIdx: Int,
            @Body body: CommentWriteData
    ): Observable<RegisterResultData>

    @DELETE("/v1/diary/{article}/comment/{comment}")
    fun deleteRecordComment(
            @Path("article") idx: Int,
            @Path("comment") commentIdx: Int
    ): Observable<BaseResultData>

    @POST("/v1/diary/{article}/like")
    fun recordLike(
            @Path("article") idx: Int
    ): Observable<BaseResultData>

    @PUT("/v1/diary/{article}/unlike")
    fun recordUnlike(
            @Path("article") idx: Int
    ): Observable<BaseResultData>

    @GET("/v1/company")
    fun getCompanyList(): Observable<CompanyListResult>

    @POST("/v1/question")
    fun addQuestion(
        @Body body: QuestionAddData
    ): Observable<BaseResultData>

    @GET("/v1/banner")
    fun getBannerInfo(): Observable<BannerListResult>

    @GET("/v1/classroom/made/{page}")
    fun getMadeClassList(
            @Path ("page") page: Int
    ): Observable<MadeClassResult>

    @GET("/v1/classroom/joined/{page}")
    fun getJoinedClassList(
            @Path ("page") page: Int
    ): Observable<JoinedClassResult>

    @POST("/v1/classroom/{classroom}/user")
    fun getClassUserList(
            @Path ("classroom") idx: Int,
            @Body body: ClassUserListRequestData
    ): Observable<ClassUserListResult>

    @PUT("/v1/classroom/{classroom}/user/{user}")
    fun updateClassMemberInfo(
            @Path ("classroom") idx: Int,
            @Path ("user") uuid: String,
            @Body body: ClassUserListRequestData
    ): Observable<BaseResultData>

    @GET("/v1/experience/{article}/review")
    fun getExperienceReview(
            @Path ("article") idx: Int
    ): Observable<ReviewListResult>

    @POST("/v1/experience/{article}/review")
    fun registerExpReview(
            @Path("article") idx: Int,
            @Body body: ReviewRegisterData
    ): Observable<BaseResultData>

    @PUT("/v1/experience/{article}/review/{review}")
    fun updateExpReview(
            @Path("article") idx: Int,
            @Path("review") rIdx: Int,
            @Body body: ReviewRegisterData
    ): Observable<BaseResultData>

    @DELETE("/v1/experience/{article}/review/{review}")
    fun deleteExpReview(
            @Path("article") idx: Int,
            @Path("review") rIdx: Int
    ): Observable<BaseResultData>

    @GET("/v1/experience/made/{page}")
    fun getMadeExperienceList(
            @Path("page") page: Int
    ): Observable<MadeExperienceResult>

    @GET("/v1/experience/response/{article}")
    fun getRequestMemberList(
            @Path("article") idx: Int
    ): Observable<RequestMemberListResult>

    @PUT("/v1/experience/response/{article}/{user}")
    fun chageReceiveRequestState(
            @Path("article") idx: Int,
            @Path("user") uuid: String,
            @Body body: ClassUserListRequestData
    )

    @GET("/v1/experience/joined/{page}")
    fun getRequestExperienceList(
            @Path("page") page: Int
    ): Observable<RequestExperienceResult>

    @PUT("/v1/experience/joined/cancel/{article}")
    fun cancelRequesExperience(
            @Path("article") idx: Int
    ): Observable<BaseResultData>

    @POST("/v1/experience/{article}/request")
    fun requestExperience(
            @Path("article") idx: Int,
            @Body body: ExperienceRequestData
    ): Observable<BaseResultData>

    @PUT("/v1/experience/joined/cancel/{article}")
    fun cancelRequestExp(
            @Path("article") idx: Int
    ): Observable<BaseResultData>

    @GET("/v1/crawling/news/{offset}/{page}")
    fun getNewsFeed(
            @Path("offset") offset: Int,
            @Path("page") page: Int
    ): Observable<NewsFeedResult>

    @GET("/v1/experience/{article}/secure_code/{secure_code}")
    fun authExperienceSecureCode(
            @Path("article") idx: Int,
            @Path("secure_code") code: String
    ): Observable<BaseResultData>

    @GET("getVltrSearchWordList")
    fun getVolunteerApplyInfo(
            @Query("ServiceKey") key: String,
            @Query("_type") type: String,
            @Query("numOfRows") rows: Int
    ): Observable<VolunteerParticipantInfoResult>

    @GET("/v1/portfolio/{user}")
    fun getExistPortfolio(
            @Path("user") user: String
    ): Observable<ExistingPortfolioResult>

    @POST("/v1/portfolio/{user}")
    fun registPortfolio(
            @Path("user") user: String,
            @Body body: PortfolioRegisterData
    ): Observable<BaseResultData>
}