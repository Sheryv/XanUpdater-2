package sheryv.updater.model

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

public class Pack(
        public val packName: String,
        public val packVersion: String,
        public val packAuthor: String,
        public val mcVersion: String,
        public val packUrl: String,
        public val usePackUrl: Boolean,
        public val website: String,
        public val forgeVersion: String,
        public val forgeUrl: String,
        public var mods: ArrayList<Mod> = ArrayList()
) {
    public val shouldDownloadConfigs: Boolean = true
    public val packUpdateDateString: String
    public val packUpdateDate: Long
    public var configs: ArrayList<Mod>? = null

    public var archivesOfConfigsUrl: String? = null

    @Transient
    private  var configPackage: String? = null


    init {
        val l = LocalDateTime.now()
        packUpdateDate = l.atZone(ZoneId.systemDefault())
                .toInstant().toEpochMilli()
        packUpdateDateString = l.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    fun fetchUrlOfArchivesConfig():String? {
        return archivesOfConfigsUrl ?: configPackage
    }

    override fun toString(): String {
        return "Name: $packName\n" +
                "Version: $packVersion\n" +
                "Minecraft version: $mcVersion\n" +
                "Forge ver.: $forgeVersion\n" +
                "Date: $packUpdateDateString\n" +
                "Mods: ${mods.size}"

    }
}