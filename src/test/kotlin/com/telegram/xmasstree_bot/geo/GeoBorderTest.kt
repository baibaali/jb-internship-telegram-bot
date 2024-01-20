package com.telegram.xmasstree_bot.geo

import com.telegram.xmasstree_bot.exception.GeoBorderException
import com.telegram.xmasstree_bot.exception.InvalidArgumentException
import com.telegram.xmasstree_bot.geo.GeoBorder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class GeoBorderTest {
    @Test
    fun createPolygonFromGeoJsonWithValidFilePath() {
        val geoBorder = GeoBorder()
        geoBorder.createPolygonFromGeoJson("/static/prague.geojson")
    }

    @Test
    fun createPolygonFromGeoJsonWithInvalidFilePath() {
        val geoBorder = GeoBorder()
        assertThrows<GeoBorderException>{geoBorder.createPolygonFromGeoJson("/static/invalid.geojson")}
    }

    @Test
    fun testPointsWithValidCoordinates() {
        val geoBorder = GeoBorder()
        geoBorder.createPolygonFromGeoJson("/static/prague.geojson")

        /* Testing points that are within the Prague city border */
        assert(geoBorder.testPoints(50.0874654, 14.4212535))
        assert(geoBorder.testPoints(50.039613, 14.421789))
        assert(geoBorder.testPoints(50.03223466581519,14.437514084440274))
        assert(geoBorder.testPoints(49.96699341978388,14.430788922101815))
        assert(geoBorder.testPoints(49.981099399119145,14.466238325338669))
        assert(geoBorder.testPoints(50.10666817235389,14.269305275108186))

        /* Testing points that are outside the Prague city border */
        assert(!geoBorder.testPoints(49.98093265303984,14.466413280465575))
        assert(!geoBorder.testPoints(50.019943247321386,14.296604428433636))
        assert(!geoBorder.testPoints(49.99298797750871,14.33570244189208))
        assert(!geoBorder.testPoints(49.96379240627416,14.325292258049004))
        assert(!geoBorder.testPoints(49.95958364157084,14.323381967616044))
        assert(!geoBorder.testPoints(49.94831851539272,14.337700858686446))
    }

    @Test
    fun testPointsWithEdgeCoordinates() {
        val geoBorder = GeoBorder()
        geoBorder.createPolygonFromGeoJson("/static/prague.geojson")

        /* Testing points that are out of ranges of latitude and longitude */
        assertThrows<InvalidArgumentException>{geoBorder.testPoints(91.0, 0.0)}
        assertThrows<InvalidArgumentException>{geoBorder.testPoints(-91.0, 0.0)}
        assertThrows<InvalidArgumentException>{geoBorder.testPoints(0.0, 181.0)}
        assertThrows<InvalidArgumentException>{geoBorder.testPoints(0.0, -181.0)}

        /* Testing points that are on the edge of the ranges of latitude and longitude */

        assertDoesNotThrow{geoBorder.testPoints(90.0, 0.0)}
        assertDoesNotThrow{geoBorder.testPoints(-90.0, 0.0)}
        assertDoesNotThrow{geoBorder.testPoints(0.0, 180.0)}
        assertDoesNotThrow{geoBorder.testPoints(0.0, -180.0)}
    }
}
