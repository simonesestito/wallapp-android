package it.simonesestito.wallapp.di.component

import dagger.Component
import it.simonesestito.wallapp.backend.service.PreviewService
import it.simonesestito.wallapp.di.module.ThreadModule


@Component(modules = [ThreadModule::class])
interface ServiceInjector {
    fun inject(previewService: PreviewService)
}