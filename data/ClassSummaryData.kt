package vdream.vd.com.vdream.data

class ClassSummaryData {
    var id = 0
    var imageUrl = ""
    var title = ""
    var tag = ""

    constructor(){}
    constructor(id: Int, url: String, title: String, tag: String){
        this.id = id
        imageUrl = url
        this.title = title
        this.tag = tag
    }
}