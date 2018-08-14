package it.simonesestito.wallapp.dagger.component

import dagger.Component
import dagger.Reusable
import it.simonesestito.wallapp.backend.service.PreviewService
import it.simonesestito.wallapp.dagger.module.ThreadModule


@Component(modules = [ThreadModule::class])
interface ServiceInjector {
    fun inject(previewService: PreviewService)
}