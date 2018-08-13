package it.simonesestito.wallapp.dagger.annotation

import androidx.lifecycle.ViewModel
import dagger.MapKey
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelMapKey(val key: KClass<out ViewModel>)

// Docs about @MapKey
//
// Identifies annotation types that are used to associate keys
// with values returned by provider methods in order to compose a map.