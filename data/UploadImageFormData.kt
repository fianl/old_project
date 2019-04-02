package vdream.vd.com.vdream.data

class UploadImageFormData {
    var file_name = ""
    var uploaded_path = ""

    constructor(filename: String, uploadedPath: String){
        this.file_name = filename
        this.uploaded_path = uploadedPath
    }
}