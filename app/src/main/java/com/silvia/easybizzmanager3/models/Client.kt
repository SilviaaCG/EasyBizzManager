package com.silvia.easybizzmanager3.models

import android.media.Image
import android.net.Uri
import android.provider.ContactsContract.CommonDataKinds.Email

data class Client(val id:String,val image:String, val nombre:String, val apellidos:String, val number:NumberContact, val direccion:String, val correo:String, val favorito:Boolean)

data class NumberContact(val prefix:String, val number:Int) {

}
