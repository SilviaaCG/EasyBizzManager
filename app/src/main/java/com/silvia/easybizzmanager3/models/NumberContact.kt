package com.silvia.easybizzmanager3.models

data class NumberContact(
    val prefix:String,
    val number:Int) {
    constructor() : this(
        "",
        0
    )
}
