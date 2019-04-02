package vdream.vd.com.vdream.data

class AnnounceData {
    var writerImgUrl = ""
    var title = ""
    var writerName = ""
    var time = ""
    var textContent = ""
    var imgUrlList: Array<String>? = null
    var attchUrlList: Array<AttachedFileData>? = null

    constructor(){
    }

    constructor(url: String, title: String, name: String, time: String, textContent: String, imgList: Array<String>?, attchList: Array<AttachedFileData>?){
        this.writerImgUrl = url
        this.title = title
        this.writerName = name
        this.time = time
        this.textContent = textContent
        this.imgUrlList = imgList
        this.attchUrlList = attchList
    }
}