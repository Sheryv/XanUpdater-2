package sheryv.updater.manager

import javafx.application.Platform
import javafx.collections.ObservableList
import sheryv.updater.Controller
import sheryv.updater.model.Pack
import sheryv.updater.model.StatusEnum
import sheryv.updater.model.TableEntry
import sheryv.updater.util.ProgressCallback
import java.io.*
import java.net.URL
import java.nio.channels.Channels
import javax.net.ssl.HttpsURLConnection


object Downloader {

    fun downloadFile(urlStr: String, file: File): File? {
        if (file.exists())
            file.delete()

        var conn: HttpsURLConnection? = null
        try {
            val url = URL(urlStr)
            conn = url.openConnection() as HttpsURLConnection
            Channels.newChannel(conn.inputStream).use { rbc ->
                FileOutputStream(file).use { fos ->
                    //conn = (HttpsURLConnection) url.openConnection();
                    val len = conn.contentLengthLong
                    var done: Long = 0
                    do {
                        done = fos.channel.transferFrom(rbc, done, java.lang.Long.MAX_VALUE)
                    } while (done < len)
                }
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        } finally {
            if (conn != null)
                conn.disconnect()
        }
        return file
    }

    fun downloadText(url: String): String? {
        var response: StringBuilder? = null
        try {
            val website = URL(url)
            val connection = website.openConnection()
            val read = BufferedReader(
                    InputStreamReader(
                            connection.getInputStream()))

            response = StringBuilder()
            var inputLine: String?
            inputLine = read.readLine()
            while (inputLine != null) {
                response.append(inputLine + "\n")
                inputLine = read.readLine()
            }
            read.close()
            return response.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun downloadTextAsync(url: String, onFinish: (res: String) -> Unit) {
//        Thread {
//            downloadText(url)?.let { onFinish(it) }
//        }.start()
        val t = Thread(Task<String>({ progress -> downloadText(url) ?: "" }, { onFinish(it) }))
        t.isDaemon = true
        t.start()

    }

    fun downloadFileAsync(url: String, file: File, onFinish: (File?) -> Unit) {
        val t = Thread(Task<File?>({ progress -> downloadFile(url, file) }, { onFinish(it) }))
    }

    fun downloadPack(tableData: ObservableList<TableEntry>, pack: Pack, folder: File, onFinish: (Unit) -> Unit, onProgress: ProgressCallback) {
        if (Controller.instance.cbWithConfig.isSelected && pack.shouldDownloadConfigs && pack.fetchUrlOfArchivesConfig() != null) {
            ModsManager.loadConfigs(pack)
        }

        val t = Thread(Task<Unit>(onCall = { p ->
            var i = 0

            for (mod in tableData) {
                val file = File(folder.getPath() + File.separator + mod.mod.name)
                downloadMod(mod, file, p, folder)



                p(i, tableData.filter { it.selected && it.status == StatusEnum.ForDownload }.size + i, null)
                i++
//                if (mod.isDependency()) {
//                    flag = FilesProcessor.unzip(file, folder)
//                    //                    if (!file.delete()) {
//                    //                        publish("ERROR: File not deleted: " + file.getPath());
//                    //                    }
//                }
            }
        }, onFinish = onFinish, onProgress = onProgress))
        t.isDaemon = true
        t.start()


    }

    fun downloadMod(mod: TableEntry, file: File, progress: ProgressCallback, folder: File): Boolean {
        if (mod.selected) {
            when (mod.status) {
                StatusEnum.NothingToDo, StatusEnum.ForUpdate -> {
                    /*         if (mod.getStatus() === StatusEnum.ForUpdate)
                                 File(mod.getOldFileName()).delete()
                             else
                                 File(mod.getName()).delete()
                             val url = ConfigGenerator.getModsFolderUrl(config, mod.getName())
                             //                            downloadFile(url, folder);
                             try {
                                 downloadFile(url, file)
                             } catch (e: IOException) {
                                 var v = ""
                                 for (stackTraceElement in e.stackTrace) {
                                     v += stackTraceElement.toString()
                                 }
                                 publish("EXCEPTION: " + e.message + " | " + v)
                             }*/
                    if (file.exists()) {
                        progress(-1, -1, "Finished downloading " + mod.mod.name)
                        return true
                    } else {
                        progress(-1, -1, "File doesn't exist " + mod.mod.name)
                    }
                }
                StatusEnum.ForDownload -> {
                    val shortName = mod.mod.shortName()
                    for (f in folder.listFiles()) {
                        if (f.name.toLowerCase().contains(shortName.toLowerCase())) {
                            f.delete()
                            break
                        }
                    }
                    val url: String = mod.mod.url ?: Controller.instance.currentPack.packUrl + mod.mod.name
                    progress(-1, -1, "Downloading " + url)
                    try {
                        downloadFile(url, file)
                    } catch (e: IOException) {
                        var v = ""
                        for (stackTraceElement in e.stackTrace) {
                            v += stackTraceElement.toString()
                        }
                        progress(-1, -1, "EXC: " + v)
                    }
                    if (file.exists()) {
                        progress(-1, -1, "Finished downloading " + mod.mod.name)
                        return true
                    } else {
                        progress(-1, -1, "Error downloading " + mod.mod.name)
                    }
                }
                else -> return true
            }
            return false
        }
        return true
    }

    class Task<T>(
            private val onCall: (progress: ProgressCallback) -> T,
            private val onFinish: (T) -> Unit,
            private val onProgress: ProgressCallback? = null
    ) : javafx.concurrent.Task<T>() {
        override fun call(): T {
            return onCall({ d, c, m -> progress(d, c, m) })
        }

        override fun succeeded() {
            super.succeeded()
            onFinish(get())
        }

        private fun progress(done: Int, count: Int, m: String?) {
            if (onProgress != null)
                Platform.runLater(Runnable { onProgress.invoke(done, count, m) })
        }

    }


    fun start(onProgress: ProgressCallback) {
        Thread {
            //                doInBackground()
        }.start()
    }
/*
        fun doInBackground(): Boolean? {
            var flag = true
            if (config.isShouldDownloadConfigs())
                downloadConfigPackage(config)

            FilesProcessor.reloadStructure(config, folder)
            FilesProcessor.deleteFiles(config, folder)
            FilesProcessor.reloadStructure(config, folder)
            for (mod in config.getMods()) {
                val file = File(folder.getPath() + File.separator + mod.getName())
                flag = downloadMod(mod, file)
                if (mod.isDependency()) {
                    flag = FilesProcessor.unzip(file, folder)
                    //                    if (!file.delete()) {
                    //                        publish("ERROR: File not deleted: " + file.getPath());
                    //                    }
                }
            }
            downloadOptionsFile()

            return flag
        }

*/

/*
        fun process(chunks: List<String>?) {
            //            StringBuilder v= new StringBuilder();
            MainForm.reloadConfig(false)
            for (c in chunks!!) {
                //  v.append(c).append("\n");
                Utils.lns(c)
                if (c.contains("Finished downloading ")) {
                    val left = UpdaterConfigManager.getSelectedCount()
                    if (left < modsToDownload) {
                        val progress = ((modsToDownload.toFloat() - left) / modsToDownload * 100).toInt()
                        MainForm.setProgress(progress)
                    }
                }
            }

        }

       fun done() {
            MainForm.dowloadingInProgress = false
            try {
                val s = get()
                if (s) {
                    Utils.lns("Pack updated successfully")
                    MainForm.setProgress(99)
                } else {
                    Utils.lns("There were errors while downloading")
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            }

        }*/

}