package uji.es.intermaps.Model

class User (
    var email: String,
    var defaultVehicle: String,
    ){

    fun setVehicle (defaultVehicle: String){
        this.defaultVehicle = defaultVehicle
    }

    fun getVehicle () : String{
        return this.defaultVehicle
    }

}

