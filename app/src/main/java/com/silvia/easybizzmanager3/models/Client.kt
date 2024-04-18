package com.silvia.easybizzmanager3.models

import android.media.Image
import android.net.Uri
import android.provider.ContactsContract.CommonDataKinds.Email
import java.util.UUID

data class Client(val id:String, val image:String, val nombre:String, val apellidos:String, val number:NumberContact, val direccion:String, val correo:String, val favorito:Boolean){
    constructor() : this("0", "","","",
        NumberContact("+34",0),"","",false
    )
}
fun generarId():String{
    return UUID.randomUUID().toString()
}

data class NumberContact(val prefix:String, val number:Int) {
    constructor() : this("", 0)
}

