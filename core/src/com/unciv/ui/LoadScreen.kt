package com.unciv.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Json
import com.unciv.UnCivGame
import com.unciv.logic.GameInfo
import com.unciv.logic.GameSaver
import com.unciv.ui.cityscreen.addClickListener
import com.unciv.ui.pickerscreens.PickerScreen
import com.unciv.ui.utils.CameraStageBaseScreen
import com.unciv.ui.utils.disable
import com.unciv.ui.utils.enable
import com.unciv.ui.utils.setFontColor
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.text.SimpleDateFormat
import java.util.*

class LoadScreen : PickerScreen() {
    lateinit var selectedSave:String

    init {
        val saveTable = Table()


        val deleteSaveButton = TextButton("Delete save", CameraStageBaseScreen.skin)
        deleteSaveButton .addClickListener {
            GameSaver().deleteSave(selectedSave)
            UnCivGame.Current.screen = LoadScreen()
        }
        deleteSaveButton.disable()
        rightSideGroup.addActor(deleteSaveButton)

        topTable.add(saveTable)
        val saves = GameSaver().getSaves()
        rightSideButton.setText("Load game")
        saves.forEach {
            val textButton = TextButton(it,skin)
            textButton.addClickListener {
                selectedSave=it

                var textToSet = it

                val savedAt = Date(GameSaver().getSave(it).lastModified())
                textToSet+="\nSaved at: "+ SimpleDateFormat("dd-MM-yy HH.mm").format(savedAt)
                try{
                    val game = GameSaver().loadGame(it)
                    textToSet+="\n"+game.getPlayerCivilization()+", turn "+game.turns
                }catch (ex:Exception){
                    textToSet+="\nCould not load game!"
                }
                descriptionLabel.setText(textToSet)
                rightSideButton.setText("Load\r\n$it")
                rightSideButton.enable()
                deleteSaveButton.enable()
                deleteSaveButton.color= Color.RED
            }
            saveTable.add(textButton).pad(5f).row()
        }

        val loadFromClipboardTable = Table()
        val loadFromClipboardButton = TextButton("Load copied data",skin)
        val errorLabel = Label("",skin).setFontColor(Color.RED)
        loadFromClipboardButton.addClickListener {
            try{
                val clipbordContents = Toolkit.getDefaultToolkit().systemClipboard.getContents(null)
                var clipbordContentsString = clipbordContents.getTransferData(DataFlavor.stringFlavor).toString()
                val loadedGame = Json().fromJson(GameInfo::class.java, clipbordContentsString)
                loadedGame.setTransients()
                UnCivGame.Current.loadGame(loadedGame)
            }catch (ex:Exception){
                errorLabel.setText("Could not load game from clipboard!")
            }
        }

        loadFromClipboardTable.add(loadFromClipboardButton).row()
        loadFromClipboardTable.add(errorLabel)
        topTable.add(loadFromClipboardTable)

        rightSideButton.addClickListener {
            UnCivGame.Current.loadGame(selectedSave)
        }



    }

}