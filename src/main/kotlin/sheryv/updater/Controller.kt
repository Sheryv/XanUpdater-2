package sheryv.updater

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.*
import javafx.util.Callback
import sheryv.updater.manager.Downloader
import sheryv.updater.manager.ModsManager
import sheryv.updater.model.*
import sheryv.updater.util.Constants
import sheryv.updater.util.Constants.lg
import java.io.File
import java.lang.reflect.Type
import java.util.*
import javafx.stage.DirectoryChooser
import sheryv.updater.manager.CreationManager
import sheryv.updater.manager.FilesProcessor


class Controller {

    @FXML
    lateinit var titleBtnClose: Pane
    @FXML
    lateinit var titleBtnMinimize: Pane
    @FXML
    lateinit var titleBar: Pane
    @FXML
    lateinit var titleBarLabel: Label
    @FXML
    lateinit var windowResize: ImageView
    @FXML
    lateinit var btnDownload: Button
    @FXML
    lateinit var btnLoadPacks: Button
    @FXML
    lateinit var btnLoadLocalPack: Button
    @FXML
    lateinit var btnRescan: Button
    @FXML
    lateinit var btnChangePath: Button
    @FXML
    lateinit var btnGetForge: Button
    @FXML
    lateinit var chbVersionList: ChoiceBox<String>
    @FXML
    lateinit var lbMinecraftPath: Label
    @FXML
    lateinit var tableMods: TableView<TableEntry>
    @FXML
    lateinit var tfLog: TextArea
    @FXML
    lateinit var lbDetails: Label
    @FXML
    lateinit var progressBar: ProgressBar
    @FXML
    lateinit var lbStatus: Label
    @FXML
    lateinit var lbSelection: Label
    @FXML
    lateinit var cbWithConfig: CheckBox
    @FXML
    lateinit var mLoadSettings: MenuItem
    @FXML
    lateinit var mDownloadLauncher: MenuItem


    @FXML
    lateinit var btnConvertNames: Button
    @FXML
    lateinit var btnCompare: Button
    @FXML
    lateinit var tfCreationDirectory: TextField
    @FXML
    lateinit var btnGenerate: Button


    lateinit var currentPack: Pack
    lateinit var tableData: ObservableList<TableEntry>
    var packs: List<PackItem>? = null
    lateinit var currentPackBaseUrl: String

    val minecraftPath: String
        get() = lbMinecraftPath.text

    val minecraftModsFolder: File
        get() = File(minecraftPath + File.separator + "mods")

    val minecraftConfigFolder: File
        get() = File(minecraftPath + File.separator + "config")


    fun initialize() {
        titleBar()
        table()
        loadListeners()
        lbMinecraftPath.text = Constants.MINECRAFT_DEFAULT_PATH
        titleBarLabel.text = TITLE
    }

    public fun fillProgress(p: Double) {
        progressBar.progress = p
    }

    private fun loadListeners() {
        btnLoadPacks.setOnAction { loadPackList() }
        chbVersionList.selectionModel.selectedIndexProperty().addListener({ observable, oldValue: Number, newValue: Number -> loadPackItem(newValue) })
        btnRescan.setOnAction { ModsManager.scan(tableData) }
        btnDownload.setOnAction {
            btnDownload.isDisable = true
            btnLoadPacks.isDisable = true
            btnChangePath.isDisable = true
            chbVersionList.isDisable = true
            btnGetForge.isDisable = true
            Downloader.downloadPack(tableData, currentPack, minecraftModsFolder, onFinish = {
                Controller.instance.fillProgress(1.0)
                tfLog.text = ""
                lg("Pack loaded")
                lbStatus.text = "Finished!"
                btnDownload.isDisable = false
                btnLoadPacks.isDisable = false
                btnChangePath.isDisable = false
                chbVersionList.isDisable = false
                btnGetForge.isDisable = false
            }, onProgress = { done, count, msg ->
                if (count > 0){
                    Controller.instance.fillProgress(done.toDouble() / count)
                    ModsManager.scan(tableData)
                    lbStatus.text = "Downloading... $done/$count"
                }
                if (msg != null)
                    lg(msg)
            })
        }

        mLoadSettings.setOnAction { ModsManager.loadOptionsFile() }
        mDownloadLauncher.setOnAction { ModsManager.openInBrowser(Constants.LAUNCHER_URL, Main.app) }
        btnGetForge.setOnAction { ModsManager.openInBrowser(currentPack.forgeUrl, Main.app) }
        btnChangePath.setOnAction {
            val directoryChooser = DirectoryChooser()
            println(Constants.MINECRAFT_DEFAULT_PATH)
            directoryChooser.initialDirectory = File(Constants.MINECRAFT_DEFAULT_PATH)

            val selectedDirectory = directoryChooser.showDialog(Main.stage)
            if (selectedDirectory == null) {
            } else {
                lbMinecraftPath.setText(selectedDirectory.absolutePath)
            }
        }

        btnCompare.setOnAction {
            val directoryChooser = DirectoryChooser()
            directoryChooser.initialDirectory = File("G:\\XenyPackUpdaterData\\Server Files 1.12\\ATM3\\mods\\")
            directoryChooser.title = "Choose source directory"
            val selectedDirectory = directoryChooser.showDialog(Main.stage)
            if (selectedDirectory == null) {
            } else {
                lg("Selected"+selectedDirectory.absolutePath)
                CreationManager.compareDirs(selectedDirectory.absolutePath, tfCreationDirectory.text)
            }
        }
        btnConvertNames.setOnAction { CreationManager.convertNames(File(tfCreationDirectory.text)) }
        btnGenerate.setOnAction {
            lg(FilesProcessor.toJson(CreationManager.generate(tfCreationDirectory.text)))
        }
    }


    private fun loadPackList() {
        chbVersionList.items.clear()
        Downloader.downloadTextAsync(Constants.XENYPACK_PACK_LIST_URL, onFinish = { res: String ->
            val gson = Gson()
            val t: Type = object : TypeToken<List<PackItem>>() {}.type
            packs = gson.fromJson<List<PackItem>>(res, t)
            Collections.reverse(packs)
            packs?.forEach { packItem -> chbVersionList.items.add(packItem.version) }
            if (chbVersionList.items.size > 0)
                chbVersionList.selectionModel.selectFirst()
        })
    }

    private fun loadPackItem(index: Number) {
        currentPackBaseUrl = Constants.BASE_URL + packs!![index as Int].folder + "/"
        Downloader.downloadTextAsync(currentPackBaseUrl + Constants.UPDATER_MODPACK_SETTINGS_FILE, {
            val gson = Gson()
            currentPack = gson.fromJson(it, Pack::class.java)
            btnDownload.isDisable = false
            btnGetForge.isDisable = false
            lbDetails.text = currentPack.toString()
            ModsManager.fillTable(tableData, currentPack)
        })
    }

    private fun table() {
        val numCol = tableMods.columns[0]
        numCol.cellValueFactory = Callback { param -> param.value.id }
        val nameCol = tableMods.columns[1]
        nameCol.cellValueFactory = Callback { param -> param.value.name }
        val statusCol = tableMods.columns[2]
        statusCol.cellValueFactory = Callback { param -> param.value.statusString }


        tableMods.rowFactory = Callback { param: TableView<TableEntry>? ->
            val r = Row()
            return@Callback r
        }

        tableData = FXCollections.observableArrayList<TableEntry>(

        )
        tableMods.items = tableData
    }


    private fun titleBar() {
        val sel = "-fx-background-color: #555;"
        val unSel = "-fx-background-color: #444;"
        val selection: (MouseEvent) -> Unit = { (it.source as Pane).style = sel }
        val unSelection: (MouseEvent) -> Unit = { (it.source as Pane).style = unSel }
        titleBtnClose.setOnMouseEntered(selection)
        titleBtnClose.setOnMouseExited(unSelection)
        titleBtnMinimize.setOnMouseEntered(selection)
        titleBtnMinimize.setOnMouseExited(unSelection)
        titleBtnClose.setOnMouseClicked {
            Platform.exit()
        }
        titleBtnMinimize.setOnMouseClicked {
            Main.stage.isIconified = true
        }
        titleBarLabel.text = TITLE
        var sx = 0.0
        var sy = 0.0
        var dx = 0.0
        var dy = 0.0
        var tx = 0.0
        var ty = 0.0
        titleBar.setOnMousePressed {
            sx = Main.stage.x - it.screenX
            sy = Main.stage.y - it.screenY
        }
        titleBar.setOnMouseDragged {
            Main.stage.x = it.screenX + sx
            Main.stage.y = it.screenY + sy
        }
        windowResize.setOnMousePressed {
            dx = it.screenX
            dy = it.screenY
            tx = Main.stage.width
            ty = Main.stage.height
        }
        windowResize.setOnMouseDragged {
            Main.stage.width = it.screenX - dx + tx
            Main.stage.height = it.screenY - dy + ty
        }
    }

    init {
        instance = this
    }

    companion object {
        const val MIN_SIZE = 300.0
        const val VERSION = "2.1"
        const val TITLE = "XanUpdater ${VERSION} - Minecraft modpack updater"
        lateinit var instance: Controller
    }
}
