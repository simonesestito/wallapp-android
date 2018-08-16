package it.simonesestito.wallapp.di.annotation

import javax.inject.Named

@Named("main_thread")
@Retention(AnnotationRetention.RUNTIME)
annotation class MainHandler