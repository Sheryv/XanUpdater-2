package sheryv.updater.model

import javafx.scene.control.TableRow
import javafx.scene.control.Labeled
import javafx.scene.paint.Color
import sheryv.updater.util.Constants.css


class Row : TableRow<TableEntry>() {
    init {
        setOnMouseClicked {
            if (this.item != null) {
                this.item.selected = !this.item.selected
                updateItem(this.item, this.isEmpty)
            }
        }
    }


    override fun updateItem(item: TableEntry?, empty: Boolean) {
        super.updateItem(item, empty)

        if (item == null || empty) {
            style = ""
        } else {
            var color = Color.rgb(0, 51, 0)
            if (item.selected) {
                if (item.status == StatusEnum.NothingToDo)
                    color = Color.rgb(32, 32, 96)
                style = "-fx-background-color: ${color.css()}; -fx-border-color: #444;"
                //-fx-stroke-width: 2;
            } else {
                style = ""
            }

            //Now 'item' has all the info of the Person in this row
            /*   if (item.getName() == "Edgar") {
                   //We apply now the changes in all the cells of the row
                   for (i in 0 until children.size) {
                       (children[i] as Labeled).textFill = Color.RED
                       (children[i] as Labeled).style = "-fx-background-color: yellow"
                   }
               } else {
                   if (tableView.selectionModel.selectedItems.contains(item)) {
                       for (i in 0 until children.size) {
                           (children[i] as Labeled).textFill = Color.WHITE
                       }
                   } else {
                       for (i in 0 until children.size) {
                           (children[i] as Labeled).textFill = Color.BLACK
                       }
                   }
               }*/
        }
    }
}