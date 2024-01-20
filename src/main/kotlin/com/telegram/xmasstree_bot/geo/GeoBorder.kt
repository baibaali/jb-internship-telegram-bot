package com.telegram.xmasstree_bot.geo

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Polygon

/**
 * Class for creating a polygon from GeoJson data and testing if a point is within the polygon.
 */
class GeoBorder {

    private lateinit var border: Polygon
    private val geometryFactory = GeometryFactory()

    /**
     * Creates a polygon from GeoJson data for Prague city.
     */

    init {
        createPolygonFromGeoJson()
    }

    fun createPolygonFromGeoJson() {
        try {

            val geoJsonData = this::class.java.getResource("/static/prague.geojson")?.readText(Charsets.UTF_8)

            if (geoJsonData == null) {
                println("geoJsonData is null")
                return
            }

            val objectMapper = ObjectMapper()
            val root = objectMapper.readTree(geoJsonData)

            val coordinatesNode = root.path("features")[0].path("geometry").path("coordinates")
            val coordinates = extractCoordinates(coordinatesNode)
            border = createPolygon(coordinates)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Tests if a point is within the polygon.
     * @param latitude Latitude of the point.
     * @param longitude Longitude of the point.
     * @return True if the point is within the polygon, false otherwise.
     */
    fun testPoints(latitude: Double, longitude: Double): Boolean {
        val point = Coordinate(longitude, latitude)

        println("Point is within the polygon: ${border.contains(geometryFactory.createPoint(point))}")
        return border.contains(geometryFactory.createPoint(point))
    }

    /**
     * Creates a polygon from a list of coordinates.
     * @param coordinates List of coordinates.
     * @return Polygon created from the list of coordinates.
     */
    private fun createPolygon(coordinates: List<Coordinate>): Polygon {
        val shell = geometryFactory.createLinearRing(coordinates.toTypedArray())
        return geometryFactory.createPolygon(shell)
    }

    /**
     * Extracts coordinates from a JsonNode.
     * @param coordinatesNode JsonNode containing coordinates.
     * @return List of coordinates.
     */
    private fun extractCoordinates(coordinatesNode: JsonNode): List<Coordinate> {
        val coordinates = mutableListOf<Coordinate>()

        for (ringNode in coordinatesNode) {
            for (pointNode in ringNode) {
                val lon = pointNode[0].asDouble()
                val lat = pointNode[1].asDouble()
                coordinates.add(Coordinate(lon, lat))
            }
        }

        return coordinates
    }

    /**
     * Prints coordinates from a list of coordinates.
     * @param coordinates List of coordinates.
     */
    private fun printCoordinates(coordinates: List<Coordinate>) {
        println("Polygon Coordinates:")
        for (coordinate in coordinates) {
            println("Latitude: ${coordinate.y}, Longitude: ${coordinate.x}")
        }
    }
}