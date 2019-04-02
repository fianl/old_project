package vdream.vd.com.vdream.data

class ExperienceRegisterData {
    var is_secure = ""
    var status = ""
    var name = ""
    var phone = ""
    var address_1 = ""
    var address_2 = ""
    var lat = 0.0
    var lng = 0.0
    var price: Int? = 0
    var min_people = 0
    var max_people = 0
    var secure_code = ""
    var title = ""
    var summary = ""
    var content = ""
    var expired_at = ""
    var opened_at = ""
    var images: Array<UploadImageFormData>? = null
    var files: Array<FileData>? = null
    var tags: Array<String>? = null
    var deleted: Array<Int>? = null
}