package uji.es.intermaps.ViewModel

import uji.es.intermaps.Exceptions.NotSuchPlaceException
import uji.es.intermaps.Interfaces.FuelAPIRepository
import uji.es.intermaps.Model.RetrofitConfig

class FuelRepository : FuelAPIRepository{
    val fuelService = RetrofitConfig.createRetrofitFuelPrice()


    override suspend fun getRegionID(region : String): String {
        var regionID: String? = null
        try{
            val response = fuelService.getRegionID(
                region
            )
            val res = response.features
            if (res.isNotEmpty()){
                val feature = res[0]
                regionID = feature.properties.region
            }
        }catch (e: Exception){
            throw NotSuchPlaceException()
        }
        return regionID ?: "Provincia no disponible"
    }


}