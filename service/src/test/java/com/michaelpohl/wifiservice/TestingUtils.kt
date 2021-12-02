package com.michaelpohl.wifiservice

import timber.log.Timber
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

/**
 * This extension returns a property of a Class as an accessible [KProperty1]
 * The property's value can then be accessed via .get(TheClassInQuestion)
 * This function is supposed to be used in testing and not intended for production code!
 */
fun <Type>Any.makeAccessibleProperty(name: String): KProperty1<Type, *>? {
    val property = this.javaClass.kotlin.memberProperties.find { it.name == name }
    if (property?.isAccessible == true) Timber.w(
        "Using accessPrivateProperty on a non-private property is unnecessary and should be removed!"
    )
    property?.isAccessible = true
    return property as? KProperty1<Type, *>
}

/**
 * Sets a class's private property to a new value.
 * This function is supposed to be used in testing and not intended for production code!
 */
fun <Type>Any.changePrivatePropertyTo(propertyName: String, newValue: Type) {
    val property = this.makeAccessibleProperty<Type>(propertyName)
    property!!.javaField!!.set(this, newValue)
}

/**
 * Makes a class's private function accessible.
 * This function is supposed to be used in testing and not intended for production code!
 */
fun Any.makeAccessibleFunction(name: String): KFunction<*>? {
    val function = this::class.declaredMemberFunctions.find { it.name == name}
    function!!.isAccessible = true
    return function
}
