package sheryv.updater.util

import javafx.scene.paint.Color
import sheryv.updater.Controller
import sheryv.updater.model.Pack
import java.io.File
import kotlin.math.roundToInt

typealias ProgressCallback = (done:Int, count:Int, msg:String?) -> Unit

object Constants {

    object V31 {
        @Deprecated("")
        val XENYPACK_PACKAGE_FILE_URL = BASE_URL + "3/" + MODS_CONFIG_FILES_ZIP
        const internal val PACK_VERSION = "3.1.4"
        const internal val MC_VERSION = "1.10.2"
        const internal val DOWNLOAD_URL = BASE_URL + "3/mods/"
        const internal val FORGE_VERSION = "1.10.2-12.18.3.2316"
        const internal val FORGE_URL = "https://files.minecraftforge.net/maven/net/minecraftforge/forge/1.10.2-12.18.3.2316/forge-1.10.2-12.18.3.2316-installer.jar"
    }

    object V32 {
        const internal val FOLDER = "400"
        const internal val PACK_VERSION = "4.0.0"
        const val MODPACK_CONFIG_URL = BASE_URL + FOLDER + "/" + MODS_CONFIG_FILES_ZIP
        const internal val MC_VERSION = "1.12.2"
        const internal val DOWNLOAD_URL = BASE_URL + FOLDER + "/mods/"
        const internal val FORGE_VERSION = "1.12.2-14.23.0.2491"
        const internal val FORGE_URL = "https://files.minecraftforge.net/maven/net/minecraftforge/forge/1.12.2-14.23.0.2491/forge-1.12.2-14.23.0.2491-installer.jar"
    }


    const val BASE_URL = "https://raw.githubusercontent.com/Detronit/xenypack/master/data/"
    const internal val PACK_WEBSITE = "https://github.com/Detronit/xenypack"
    const internal val PACK_NAME = "XenyPack"
    const internal val PACK_AUTHOR = "Sheryv"


    const val UPDATER_MODPACK_SETTINGS_FILE = "modpack_config.json"
    const val UPDATER_PACK_LIST = "packs.json"
    const val MODS_CONFIG_FILES_ZIP = "config_package.zip"
    const val LAUNCHER_URL = BASE_URL + "Shiginima-Launcher.jar"
    const val MINECRAFT_OPTIONS_FILE_NAME = "options.txt"

    val MINECRAFT_DEFAULT_PATH = System.getenv("APPDATA")+ File.separator+".minecraft"

    const val MINECRAFT_OPTIONS_URL = BASE_URL + MINECRAFT_OPTIONS_FILE_NAME


    const val XENYPACK_PACK_LIST_URL = BASE_URL + UPDATER_PACK_LIST


    const val HELP_2 = "http://xenypack.y0.pl/xenypack/forge.gif"
    const val HELP_1 = "http://xenypack.y0.pl/xenypack/start.gif"

    val packV33: Pack = Pack("XenyPack",
            "3.3.1",
            "Sheryv",
            "1.12.2",
            BASE_URL+"33/mods/",
            true,
            "https://github.com/Detronit/xenypack/",
            "14.23.1.2568",
            "https://files.minecraftforge.net/maven/net/minecraftforge/forge/1.12.2-14.23.1.2568/forge-1.12.2-14.23.1.2568-installer.jar"
    )

    init {
        packV33.archivesOfConfigsUrl = BASE_URL+"33/"+ MODS_CONFIG_FILES_ZIP
    }

    fun lg(msg:String, color:Color = Color.LIGHTGRAY){
        Controller.instance.tfLog.appendText(msg+"\n")
    }

    fun Color.css(): String {
        return String.format("#%02X%02X%02X", (this.red*256).toInt(), (this.green*256).toInt(), (this.blue*256).toInt())
    }
}