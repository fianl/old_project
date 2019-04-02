package vdream.vd.com.vdream.data

class RegisterExperienceData {
    var is_secure = ""
    var status = ""
    var name = ""
    var phone = ""
    var address_1 = ""
    var address_2 = ""
    var lat = 0.0
    var lng = 0.0
    var price = 0
    var people = 0
    var secure_code = ""
    var title = ""
    var content = ""
    var images: Array<UploadImageFormData>? = null
    var files: Array<FileData>? = null
    var tags: Array<String>? = null
}