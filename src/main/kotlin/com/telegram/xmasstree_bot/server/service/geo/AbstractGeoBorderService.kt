package com.telegram.xmasstree_bot.server.service.geo

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.telegram.xmasstree_bot.exception.GeoBorderException
import com.telegram.xmasstree_bot.exception.InvalidArgumentException
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Polygon

abstract class AbstractGeoBorderService: GeoBorderService {
    private lateinit var border: Polygon
    private val geometryFactory = GeometryFactory()

    init {
        initBorder()
    }

    private fun initBorder() {
        try {
            loadGeoJsonData()
        } catch (e: GeoBorderException) {
            e.printStackTrace()
        }
    }

    /**
     * Loads GeoJson data.
     * @throws GeoBorderException if GeoJson data is null or cannot be read.
     */
    @Throws(GeoBorderException::class)
    protected abstract fun loadGeoJsonData()

    /**
     * Creates a polygon from GeoJson data for Prague city.
     * @throws GeoBorderException if GeoJson data is null or cannot be read.
     */
    @Throws(GeoBorderException::class)
    protected fun createPolygonFromGeoJson(filePath: String) {
        try {
            val geoJsonData = this::class.java.getResource(filePath)?.readText(Charsets.UTF_8)
                ?: throw GeoBorderException("GeoJson data is null")

            val objectMapper = ObjectMapper()
            val root = objectMapper.readTree(geoJsonData)

            val coordinatesNode = root.path("features")[0].path("geometry").path("coordinates")
            val coordinates = extractCoordinates(coordinatesNode)
            border = createPolygon(coordinates)
        } catch (e: GeoBorderException) {
            throw GeoBorderException("GeoJson data is null or cannot be read")
        }
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
     * Tests if a point is within the polygon.
     * @param latitude Latitude of the point.
     * @param longitude Longitude of the point.
     * @throws InvalidArgumentException if latitude or longitude is not within the range.
     * @throws GeoBorderException if polygon is not initialized.
     * @return True if the point is within the polygon, false otherwise.
     */
    @Throws(InvalidArgumentException::class, GeoBorderException::class)
    override fun testPoint(latitude: Double, longitude: Double): Boolean {
        if (latitude < -90.0 || latitude > 90.0 || longitude < -180.0 || longitude > 180.0) {
            throw InvalidArgumentException("Latitude must be between -90 and 90, longitude between -180 and 180")
        }

        if (!this::border.isInitialized) {
            throw GeoBorderException("Polygon is not initialized")
        }

        val point = Coordinate(longitude, latitude)
        return border.contains(geometryFactory.createPoint(point))
    }

}