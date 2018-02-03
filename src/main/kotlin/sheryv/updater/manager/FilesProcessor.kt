package sheryv.updater.manager

import com.google.gson.GsonBuilder
import sheryv.updater.util.Constants.lg
import java.io.*
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object FilesProcessor {




    fun saveFile(content: String, pathString: String): File {
        val f = File(pathString)
        val c: Boolean
        if (!f.exists()) {
            try {
                File(f.parent).mkdirs()
                c = f.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        } else {
            f.delete()
        }
        val path = f.toPath()
        try {
            Files.newBufferedWriter(path, StandardCharsets.UTF_8).use { writer ->
                //        try {
                //            Files.write(path, content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
                writer.write(content)
                writer.flush()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return f
    }

    fun readFile(file: File): String {
        try {
            return Files.readAllLines(file.toPath()).joinToString("\n")
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return ""
    }


    private const val BUFFER_SIZE = 4096
    /**
     * Extracts a zip file specified by the zipFile to a directory specified by
     * destDirectory (will be created if does not exists)
     *
     * @param zipFile
     * @param destDirectory
     * @throws IOException
     */
    fun unzip(zipFile: File, destDirectory: File): Boolean {
        if (!destDirectory.exists()) {
            destDirectory.mkdir()
        }
        try {
            val zipIn = ZipInputStream(FileInputStream(zipFile))
            var entry: ZipEntry? = zipIn.nextEntry
            // iterates over entries in the zip file
            while (entry != null) {
                var name = entry.name
                if (name[name.length - 1] == '/') {
                    name = name.substring(0, name.length - 1)
                }
                val filePath = destDirectory.toString() + File.separator + name
                if (!entry.isDirectory) {
                    // if the entry is a file, extracts it
                    if (extractFile(zipIn, filePath)) {
                        lg("File overridden: " + filePath)
                    }
                } else {
                    // if the entry is a directory, make the directory
                    val dir = File(filePath)
                    dir.mkdir()
                }
                zipIn.closeEntry()
                entry = zipIn.nextEntry
            }
            zipIn.close()
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }

    }

    /**
     * Extracts a zip entry (file entry)
     *
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun extractFile(zipIn: ZipInputStream, filePath: String): Boolean {
        val f = File(filePath)
        var flag = false
        if (f.exists())
            flag = f.delete()
        val bos = BufferedOutputStream(FileOutputStream(f))
        val bytesIn = ByteArray(BUFFER_SIZE)
        var read = 0
        read = zipIn.read(bytesIn)
        while (read != -1) {
            bos.write(bytesIn, 0, read)
            read = zipIn.read(bytesIn)
        }
        bos.close()
        return flag
    }

    fun <T>toJson(o: T): String {
        val json = GsonBuilder().setPrettyPrinting().create()
        return json.toJson(o)
    }
}