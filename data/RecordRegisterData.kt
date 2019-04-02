package vdream.vd.com.vdream.data

open class RecordRegisterData {
    var status = ""
    var kind: String? = null
    var class_unit: String? = ""
    var class_learning_content: String? = null
    var class_leading_learning: String? = null
    var club_topic: String? = null
    var club_activated_at: String? = null
    var club_participants: String? = null
    var club_motivation: String? = null
    var club_principle: String? = null
    var club_learning: String? = null
    var career_activated_at: String? = null
    var career_effort: String? = null
    var career_learning: String? = null
    var contest_activated_at: String? = null
    var contest_effort: String? = null
    var contest_learning: String? = null
    var volunteer_kind: String? = null
    var volunteer_place: String? = null
    var volunteer_started_at: String? = null
    var volunteer_ended_at: String? = null
    var volunteer_period: Int? = null
    var volunteer_activity_content: String? = null
    var volunteer_effort: String? = null
    var volunteer_learning: String? = null
    var behavior_kind: String? = null
    var behavior_case: String? = null
    var reading_book_name: String? = null
    var reading_author: String? = null
    var reading_motivation: String? = null
    var reading_summary: String? = null
    var reading_learning: String? = null
    var content: String? = null
    var images: Array<UploadImageFormData>? = null
    var files: Array<UploadImageFormData>? = null
    var tags: Array<String>? = null
    var deleted: Array<Int>? = null
}