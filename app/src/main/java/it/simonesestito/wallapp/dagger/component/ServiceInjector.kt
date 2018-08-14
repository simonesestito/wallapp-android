package it.simonesestito.wallapp.dagger.component

import dagger.Component
import it.simonesestito.wallapp.backend.service.PreviewService
import it.simonesestito.wallapp.dagger.module.ThreadModule
import javax.inject.Singleton


@Singleton
@Component(modules = [ThreadModule::class])
interface ServiceInjector {
    fun inject(previewService: PreviewService)
}