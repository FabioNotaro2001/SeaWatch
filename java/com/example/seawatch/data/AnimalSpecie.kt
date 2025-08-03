package com.example.seawatch.data

import android.widget.GridLayout.Spec

fun getAnimal(): List<String>{
    return listOf<String>("","Altro", "Balena", "Delfino", "Foca", "Razza", "Squalo", "Tartaruga", "Tonno")
}

fun getSpecieFromAniaml(animal : String): List<Speci>{
    if(animal == "Tonno"){
        return listOf<Speci>(
            Speci("",null),
            Speci("Comune","Thunnus thinnus")
        )
    } else if(animal == "Balena"){
        return listOf<Speci>(
            Speci("",null),
            Speci("Balenottera comune", "Balaenoptera physalus"),
            Speci("Megattera","Megaptera novaeangliae")
        )
    }else if(animal == "Delfino"){
        return listOf<Speci>(
            Speci("",null),
            Speci("Bianco atlantico",null),
            Speci("Cefalorinco di Commerson",null),
            Speci("Feresa",null),
            Speci("Globicefalo di Gray",null),
            Speci("Globicephala",null),
            Speci("Lagenodelfino",null),
            Speci("Peponocefalo",null),
            Speci( "Scuro",null),
            Speci("Stenella dal lungo rostro",null),
            Speci("Stenella maculata",null),
            Speci("Steno",null),
            Speci( "Comune","Delphinus delphis"),
            Speci("Globicefalo","Globicephala melas"),
            Speci("Grampo","Grampus griseus"),
            Speci("Pseudorca","Pseudorca crassidens"),
            Speci("Stennella striata","Stenella coeruleoalba"),
            Speci("Tursiope","Tursiops truncatus")
        )
    }else if(animal == "Foca"){
        return listOf<Speci>(
            Speci("",null),
            Speci("Comune",null),
            Speci("Monaca","Monachus monachus")

        )
    }else if(animal == "Razza"){
        return listOf<Speci>(
            Speci("",null),
            Speci("Bavosa", "Dipturus batiscom")
        )
    }else if(animal == "Squalo"){
        return listOf<Speci>(
            Speci("",null),
            Speci("Volpe occhio grosso","Alopias superciliosus"),
            Speci("Volpe","Alopias vulpinus"),
            Speci("Grigio del genere Carcharhinus","Carcharhinus"),
            Speci("Seta", "Carcharhinus falciformis"),
            Speci("Bianco","Carcharodon carcharias"),
            Speci("Elefante", "Cetorhinus maximus"),
            Speci("Gattuccio boccanera","Galeus melastomus"),
            Speci("Notidano grigio","Hexanchus griseus"),
            Speci("Mako","Isurus oxyrinchus"),
            Speci("Smeriglio","Lamna nasus"),
            Speci("Cagnaccio","Odontaspis ferox"),
            Speci("Verdesca","Prionace glauca"),
            Speci("Latteo","Rhizoprionodon acutus"),
            Speci("Gattuccio","Scyliorhinus canicula"),
            Speci("Martello","Sphyrna lewini"),
            Speci("Spinarolo","Squalus acanthias"),
            Speci("Pelle nera","Squatina squatina")

        )
    }else if(animal == "Tartaruga"){
        return listOf<Speci>(
            Speci("",null),
            Speci("Comune", "Caretta caretta"),
            Speci("Verde","Chelonia mydas") ,
            Speci("Liuto", "Dermochelys coriacea")

        )
    }else {
        return listOf<Speci>(
            Speci("",null),
            Speci("Pesce Luna","Mola mola"),
            Speci("Capodoglio","Physeter macrocephalus"),
            Speci("Noce di mare","Mnemiopsis leidyi"),
            Speci("Zifio","Ziphius cavirostris")
        )
    }
}