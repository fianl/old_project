package vdream.vd.com.vdream.data

class AttachedFileData {
    var fileName = ""
    var fileSize = 0
    var filePath = ""

    constructor(){}
    constructor(fileName: String, fileSize: Int, filePath: String){
        this.fileName = fileName
        this.fileSize = fileSize
        this.filePath = filePath
    }
}