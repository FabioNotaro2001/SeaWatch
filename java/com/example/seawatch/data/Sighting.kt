package com.example.seawatch.data

import java.util.Date

class Sighting(id:String,
               avvistatore:String,
               data:String,
               numeroEsemplari:String,
               posizione:String,
               animale:String,
               specie:String,
               mare:String,
               vento:String,
               note:String,
               immagine1:String = "",
               immagine2:String = "",
               immagine3:String = "",
               immagine4:String = "",
               immagine5:String = "",
               caricato:Boolean = false) {
    val id = id
    val date=data
    val animal=animale
    val user=avvistatore
    val numberOfSamples=numeroEsemplari
    val position=posizione
    val specie=specie
    val sea=mare
    val wind=vento
    val notes=note
    var image1= immagine1
    var image2= immagine2
    var image3= immagine3
    var image4= immagine4
    var image5= immagine5
    var upload=caricato

    override fun toString(): String {
        return "Sighting(date='$date', animal='$animal', user='$user', numberOfSamples='$numberOfSamples', position='$position', specie='$specie', sea='$sea', wind='$wind', notes='$notes')"
    }
}