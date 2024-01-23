package com.telegram.xmasstree_bot.server.service.geo

import com.telegram.xmasstree_bot.server.exception.FileNotLoadedException
import org.springframework.stereotype.Service

/**
 * PragueGeoBorderService class.
 * Used to check if a given point is inside Prague city.
 * @property border Polygon representing the border of Prague city.
 * @property geometryFactory GeometryFactory used to create a polygon.
 */
@Service
class PragueGeoBorderService: AbstractGeoBorderService() {
    /**
     * Loads GeoJson data.
     * @throws FileNotLoadedException if GeoJson data is null or cannot be read.
     */
    @Throws(FileNotLoadedException::class)
    override fun loadGeoJsonData() {
        createPolygonFromGeoJson("/static/prague.geojson")
    }
}