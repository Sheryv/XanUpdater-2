package sheryv.updater.model

class Mod(
        public val name: String,
        public val version: String,
        public val selected: Boolean = false,
        public val url: String? = null,
        public val website: String? = null
) {

    fun shortName():String {
        return if (name.indexOf('-') > 0) name.substring(0, name.indexOf('-')) else name
    }

    fun buildFullUrl(base:String):String{
        return base+name
    }

}