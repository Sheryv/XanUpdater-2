package sheryv.updater.model

import javafx.scene.paint.Color

enum class StatusEnum {
    ForDownload,
    ForUpdate,
    NoFileOnServer,
    NothingToDo;

    val color: Color
        get() {
            var s = Color.BLACK
            when (this) {
                ForUpdate -> s = Color.rgb(172, 117, 4)
                ForDownload -> s = Color.rgb(27, 80, 220)
                NoFileOnServer -> s = Color.RED
            }
            return s
        }

    override fun toString(): String {
        var s = ""
        when (this) {
            ForUpdate -> s = "Update required"
            ForDownload -> s = "Download required"
            NothingToDo -> s = "Mod ready, nothing to do"
            NoFileOnServer -> s = "Error, mod not found on server"
        }
        return s
    }
}