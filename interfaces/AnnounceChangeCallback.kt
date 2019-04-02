package vdream.vd.com.vdream.interfaces

interface AnnounceChangeCallback {
    fun announceDeleted()
    fun requestUpdate(type: String, idx: Int)
}