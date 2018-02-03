package sheryv.updater.model

import javafx.beans.property.SimpleStringProperty


class TableEntry{

    public val name: SimpleStringProperty
    public var statusString: SimpleStringProperty
     get() = SimpleStringProperty(status.toString())
    public var status: StatusEnum
    public var selected: Boolean = false
    public val id: SimpleStringProperty
    public val mod: Mod

    constructor(id: Int, name: String, mod: Mod){
        this.name = SimpleStringProperty(name)
        selected = mod.selected
        status = StatusEnum.ForDownload
        statusString = SimpleStringProperty(status.toString())
        this.id = SimpleStringProperty(String.format("%03d", id))
        this.mod = mod
    }



}