package uji.es.intermaps.acceptanceTests

import org.junit.Assert.assertEquals
import org.junit.Test
import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.InterestPlace

class AT_IT1 {

    @Test
    fun edit_E1Valido_setAliasToAPlaceOfInterest(){
        val coordinates = Coordinate(-18.665695,35.529562)
        val newAlias = "Mozambiquinho"
        val placeOfInterest = InterestPlace(coordinates,"Mozambique", "Moz")
        assertEquals(true, placeOfInterest.setAlias(newAlias))
    }
}