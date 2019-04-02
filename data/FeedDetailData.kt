package vdream.vd.com.vdream.data

import java.io.Serializable

class FeedDetailData: Serializable{
    var idx = 0
    var kind = ""
    var is_mine = ""
    var is_like = ""
    var is_secure = ""
    var is_notice = ""
    var nickname = ""
    var profile_img = ""
    var name = ""
    var phone = ""
    var title = ""
    var summary = ""
    var address_1 = ""
    var address_2 = ""
    var lat = ""
    var lng = ""
    var price = 0
    var min_people = 0
    var max_people = 0
    var secure_code: String? = null
    var status = ""
    var content = ""
    var video: String? = null
    var tags: Array<String>? = null
    var comments: Array<CommentData>? = null
    var files: Array<FileData>? = null
    var like_count = 0
    var opened_at = ""
    var expired_at = ""
    var created_at = ""
}