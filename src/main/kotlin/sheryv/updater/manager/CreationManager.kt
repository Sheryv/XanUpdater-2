package sheryv.updater.manager

import sheryv.updater.model.Mod
import sheryv.updater.model.Pack
import sheryv.updater.util.Constants
import sheryv.updater.util.Constants.lg
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*

object CreationManager {


    fun compareDirs(src: String, comp: String) {
        val dest = comp + (Random().nextInt(900) + 100)
        val folder = File(src)
        File(dest).mkdir()
        val compFiles = File(comp).listFiles()
        for (fileEntry in folder.listFiles()) {
            if (!fileEntry.isDirectory) {
                val name = shortName(fileEntry.name)
                compFiles.filter { file -> file.name.toLowerCase().contains(name) }.forEach {
                    val c = Paths.get(dest, fileEntry.name)
                    Files.copy(fileEntry.toPath(), c, StandardCopyOption.REPLACE_EXISTING)
                    lg("Copied to: " + c)
                }
            }
        }
    }

    fun convertNames(folder: File) {
        if (!folder.isDirectory)
            return
        for (fileEntry in folder.listFiles()!!) {
            if (!fileEntry.isDirectory) {
                val name = fileEntry.name
                var f = name
                var flag = false
                if (name.indexOf(' ') > 0) {
                    f = name.replace(' ', '-')
                    flag = true
                }
                val c = name[0]
                if (Character.isLowerCase(c)) {
                    f = c.toString().toUpperCase() + f.substring(1, f.length)
                    flag = true
                }
                if (flag) {
                    val n = File(fileEntry.parent + File.separator + f)
                    fileEntry.renameTo(n)
                    lg("Renamed from $name | to |  $f")
                }
            }
        }
        lg("Names converted")
    }

    fun generate(input: String): Pack {

        val p = Constants.packV33
        File(input).listFiles().forEach { file ->
            if (Files.isRegularFile(file.toPath())) {
                val name = file.name
                var ver = name.dropLast(4).substring(name.lastIndexOf("-")+1, name.length-4)
                val e = Mod(name, ver, true)

                p.mods.add(e)
            } else {
                lg("Not file: " + file.name)
            }
        }
        return p
    }


    private fun shortName(name: String): String {
        return if (name.indexOf('-') > 0) name.substring(0, name.indexOf('-')).toLowerCase() else name.toLowerCase()
    }

}