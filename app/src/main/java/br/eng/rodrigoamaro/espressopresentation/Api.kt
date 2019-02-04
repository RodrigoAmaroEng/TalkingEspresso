package br.eng.rodrigoamaro.espressopresentation

import io.reactivex.Observable

interface Api {
    suspend fun coroutineCall(someArgument: String): Boolean

    fun reactiveCall(someArgument: String): Observable<Boolean>
}

class RealApi : Api {
    override suspend fun coroutineCall(someArgument: String): Boolean {
        Thread.sleep(3000)
        return true
    }

    override fun reactiveCall(someArgument: String): Observable<Boolean> {
        return Observable.fromPublisher { it ->
            Thread.sleep(3000)
            it.onNext(true)
            it.onComplete()
        }
    }
}