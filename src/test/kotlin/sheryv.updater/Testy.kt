package sheryv.updater

import org.hamcrest.MatcherAssert
import org.junit.Assert
import org.junit.Test


class Testy {

    @Test
    fun test() {
        val name= "EnderStorage-1.12.2-2.4.2.126.jar"
        var ver = name.dropLast(4).substring(name.lastIndexOf("-")+1, name.length-4)
        print(ver)
    }
}