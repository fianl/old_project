package vdream.vd.com.vdream.data

import java.io.Serializable

class NewsFeedData: Serializable {
    var idx = 0
    var category1 = ""
    var category2 = ""
    var title = ""
    var content = ""
    var url = ""
    var thumbnail: String? = null
    var created_at = ""
}