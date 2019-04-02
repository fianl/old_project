package vdream.vd.com.vdream.data

import java.io.Serializable

class ClassroomBaseData : Serializable{
    var idx = 0
    var is_hidden = ""
    var is_public = ""
    var is_locale = ""
    var category: ClassroomCategoryData? = null
    var title = ""
    var profile_img = ""
    var background_img = ""
    var address_1 = ""
    var address_2 = ""
    var lat = ""
    var lng = ""
    var tags: Array<String>? = null
    var subscribe_count = 0
    var like_count = 0
    var hidden_at = ""
    var deleted_at = ""
    var created_at = ""
    var experience_count = 0
}