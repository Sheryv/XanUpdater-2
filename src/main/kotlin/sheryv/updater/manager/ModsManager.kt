package sheryv.updater.manager

import javafx.application.HostServices
import javafx.collections.ObservableList
import sheryv.updater.Controller
import sheryv.updater.model.Pack
import sheryv.updater.model.StatusEnum
import sheryv.updater.model.TableEntry
import java.io.File
import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths
import javafx.scene.control.ButtonType
import java.util.Optional
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.Alert
import sheryv.updater.util.Constants
import sheryv.updater.util.Constants.lg
import javafx.application.Application


object ModsManager {

    fun fillTable(tableData: ObservableList<TableEntry>, pack: Pack) {
        tableData.clear()
        pack.mods.forEachIndexed { index, mod ->
            tableData.add(TableEntry(index, mod.name, mod))
        }

        scan(tableData)

    }

    fun scan(tableData: ObservableList<TableEntry>) {
        val p = Controller.instance.minecraftModsFolder
        tableData.forEach { modEntry ->
            val path = Paths.get(p.path, modEntry.mod.name)
            if (path.toFile().exists()) {
                modEntry.status = StatusEnum.NothingToDo
            } else {
                modEntry.status = StatusEnum.ForDownload
            }
        }
        Controller.instance.tableMods.refresh()
    }

    fun loadConfigs(p: Pack) {
        val msg = "Do you want to download configuration files for mods? " +
                "It is highly recommended for first installation.\n" +
                "NOTE: If configuration differs between your client and server you may be unable to enter such server. " +
                "This action will override some files."
        if (showConfirm(msg)) {
            // TODO: 04.07.2017 Support for separate config files
            lg("Downloading Archives Of Configs")
            val pack = File(Controller.instance.minecraftPath + File.separator + Constants.MODS_CONFIG_FILES_ZIP)
            try {
                val t = Thread(Downloader.Task<String?>({
                    var text = ""
                    Downloader.downloadFile(p.fetchUrlOfArchivesConfig()!!, pack)
                    text = "There was error while unzipping package. You can do it manually. It was saved at: " + pack.path
                    if (FilesProcessor.unzip(pack, Controller.instance.minecraftConfigFolder)) {
                        pack.delete()
                        return@Task null
                    } else {
                        println(text)
                        return@Task text
                    }

                }, {success ->
                    if (success == null){
                        lg("Archives Of Configs loaded")
                    }else{
                        lg(success)
                    }
                }))
                t.isDaemon=true
                t.start()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun loadOptionsFile() {
        val msgOptions = "Do you want to download default options set for Minecraft?" +
                " It includes video setting, controls, etc.\n" +
                "It is recommended choice in case you install this modpack first time."
        if (showConfirm(msgOptions)) {
            val file = File(Controller.instance.minecraftPath + File.separator + Constants.MINECRAFT_OPTIONS_FILE_NAME)
            try {
                Downloader.downloadFile(Constants.MINECRAFT_OPTIONS_URL, file)
                lg("Downloaded settings file to: " + file.path)
            } catch (e: IOException) {
                lg("Error while downloading setting file")
                e.printStackTrace()
            }
        }
    }

    fun showConfirm(msg: String): Boolean {
        val alert = Alert(AlertType.CONFIRMATION)
        alert.title = "Confirmation"
        alert.headerText = msg
        //alert.contentText = "Are you ok with this?"
        return alert.showAndWait().get() == ButtonType.OK
    }

    fun openInBrowser(url:String, app: Application){
        val hostServices = app.hostServices
        hostServices.showDocument(url)
    }
}