package com.lucadenti.glaze

import io.reactivex.subjects.PublishSubject

class Watcher<T>(private val t: T) {
    /**
     * To register/publish an observable over an object on change
     */
    fun changes(): PublishSubject<T> {
        return PublishSubject.create<T>()
    }

    /**
     * To notify/emit an object level change
     */
    fun emit(ps: PublishSubject<T>) {
        ps.onNext(t)
    }
}