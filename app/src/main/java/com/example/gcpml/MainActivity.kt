package com.example.gcpml

import android.content.ContentValues.TAG
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.core.net.toUri
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.languageid.LanguageIdentification
//import com.google.mlkit.nl.translate.TranslateLanguage
//import com.google.mlkit.nl.translate.TranslateRemoteModel
//import com.google.mlkit.nl.translate.Translation
//import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var textV: TextView
    private lateinit var labelBtn: Button
    private lateinit var recogniseBtn: Button
    private lateinit var detectBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textV = findViewById(R.id.text_view)
        labelBtn = findViewById(R.id.label_image_btn)
        recogniseBtn = findViewById(R.id.recognise_text_btn)
        detectBtn = findViewById(R.id.detect_language_btn)

        labelBtn.setOnClickListener {
            labelImage()
        }

        recogniseBtn.setOnClickListener {
            recognizeText()
        }

        detectBtn.setOnClickListener {
            recognizeLanguage("Questo è inglese")
        }
    }

    // Funkcija valodas atpazīšanai
    private fun recognizeLanguage(text: String) {
        val languageIdentifier = LanguageIdentification.getClient()
        languageIdentifier.identifyLanguage(text)
            .addOnSuccessListener { languageCode ->
                if (languageCode == "und") {
                    textV.text = "Language cannot be detected. There was an error."
                } else {
                    textV.text = "Language: " + languageCode
                }
            }
            .addOnFailureListener {
                textV.text = "Language cannot be detected. There was an error."
            }
    }

    // Funkcija teksta atpazīšanai attēlā
    private fun recognizeText() {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val image: InputImage
        try {
            image = InputImage.fromFilePath(this, "file:///sdcard/Download/gramatasteksts.jpeg".toUri())
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    textV.text = "Text: " + visionText.text
                }
                .addOnFailureListener { e ->
                    textV.text = "Text cannot be recognized. There was an error."
                }
        } catch (e: IOException) {
            textV.text = "Text cannot be recognized. There was an error."
        }


    }

    // Funkcija objektu atpazīšanai attēlā
    private fun labelImage() {
        val image: InputImage
        try {
            image = InputImage.fromFilePath(this, "file:///sdcard/Download/bernu-istabas-komplekts.jpeg".toUri())
            val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
            labeler.process(image)
                .addOnSuccessListener { labels ->
                    var labelsstring = ""
                    for (label in labels){
                        if (label == labels.last()) {
                            labelsstring += label.text
                        } else {
                            labelsstring += label.text + ", "
                        }
                    }
                    textV.text = "Labels: " + labelsstring
                }
                .addOnFailureListener { e ->
                    textV.text = "Image cannot be labeled. There was an error."
                }

        } catch (e: IOException) {
            textV.text = "Image cannot be labeled. There was an error."
        }
    }
}