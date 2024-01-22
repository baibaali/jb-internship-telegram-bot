package com.telegram.xmasstree_bot.server.service.geo

import org.springframework.stereotype.Service

@Service
class PragueGeoBorderService: AbstractGeoBorderService() {
    override fun loadGeoJsonData() {
        createPolygonFromGeoJson("/static/prague.geojson")
    }
}