package vdream.vd.com.vdream.data

class BoardRegisterData {
    var is_notice = ""
    var status = ""
    var content = ""
    var video: String? = null
    var images: Array<UploadImageFormData>? = null
    var files: Array<String>? = null
    var tags: Array<String>? = null
    var deleted: Array<Int>? = null
}