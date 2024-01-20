package com.telegram.xmasstree_bot

import org.junit.jupiter.api.Test

class GeoBorderTest {
    @Test
    fun createPolygonFromGeoJson() {
        val geoBorder = GeoBorder()
        geoBorder.createPolygonFromGeoJson()
        assert(geoBorder.testPoints(50.0874654, 14.4212535))
        assert(geoBorder.testPoints(50.039613, 14.421789))
        assert(geoBorder.testPoints(50.03223466581519,14.437514084440274))
        assert(geoBorder.testPoints(49.96699341978388,14.430788922101815))
        assert(geoBorder.testPoints(49.981099399119145,14.466238325338669))
        assert(!geoBorder.testPoints(49.98093265303984,14.466413280465575))
        assert(geoBorder.testPoints(50.10666817235389,14.269305275108186))
    }
}