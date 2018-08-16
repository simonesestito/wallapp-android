package it.simonesestito.wallapp.di.module

import android.os.Handler
import android.os.Looper
import dagger.Module
import dagger.Provides
import dagger.Reusable
import it.simonesestito.wallapp.IO_EXECUTOR_MAX_THREADS
import it.simonesestito.wallapp.di.annotation.IoThread
import it.simonesestito.wallapp.di.annotation.MainHandler
import java.util.concurrent.Executors

@Module
class ThreadModule {
    @Provides
    @Reusable
    @MainHandler
    fun mainThread() = Handler(Looper.getMainLooper())

    @Provides
    @Reusable
    @IoThread
    fun ioThread() = Executors.newFixedThreadPool(IO_EXECUTOR_MAX_THREADS)!!
}