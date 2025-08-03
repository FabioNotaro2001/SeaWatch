package com.example.seawatch

import android.webkit.JavascriptInterface
import android.webkit.WebView

class MapHandler(private val webView: WebView) {
    @JavascriptInterface
    fun onMapReady() {
        // La mappa è pronta per essere utilizzata
    }

    @JavascriptInterface
    fun onMarkerClicked(markerId: String) {
        // L'utente ha cliccato su un marker sulla mappa
    }

    // Aggiungi altre funzioni se necessario
}