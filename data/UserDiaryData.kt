package vdream.vd.com.vdream.data

import java.io.Serializable

class UserDiaryData: Serializable {
    var idx = 0
    var is_mine = ""
    var is_like = ""
    var nickname = ""
    var profile_img = ""
    var status = ""
    var content = ""
    var tags: Array<String>? = null
    var comments: Array<CommentData>? = null
    var files: Array<FileData>? = null
    var like_count = 0
    var created_at = ""
}