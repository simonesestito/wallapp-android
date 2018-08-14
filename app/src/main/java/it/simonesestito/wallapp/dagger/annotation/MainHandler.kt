package it.simonesestito.wallapp.dagger.annotation

import javax.inject.Named

@Named("main_thread")
@Retention(AnnotationRetention.RUNTIME)
annotation class MainHandler