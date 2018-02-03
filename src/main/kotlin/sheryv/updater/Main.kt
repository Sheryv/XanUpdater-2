@file:JvmName("Main")
package sheryv.updater

import javafx.application.Application
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.io.File

class Main : Application() {

    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {
        stage = primaryStage
        app = this
        primaryStage.initStyle(StageStyle.UNDECORATED);
        val root: Parent = FXMLLoader.load<Parent>(javaClass.getResource("temp.fxml"))
        primaryStage.title = Controller.TITLE;
        val scene = Scene(root, 900.0, 600.0)
        primaryStage.scene = scene
        primaryStage.show()
        val styleFile = File(javaClass.getResource("style.css").path)
        val style = "file:///style.css"
//        val style = "file:///" + styleFile.absolutePath.replace("\\", "/")

        scene.stylesheets.add(javaClass.getResource("style.css").toExternalForm())
    }

    companion object {

        lateinit var stage: Stage
        lateinit var app: Application

        @JvmStatic
        fun main(args: Array<String>) {
            Application.launch(Main::class.java, *args)
        }
    }
}
