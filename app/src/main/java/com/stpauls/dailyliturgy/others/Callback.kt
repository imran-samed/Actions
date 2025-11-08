package com.stpauls.dailyliturgy.others

abstract class Callback<T> {
    abstract fun onSuccess(t:T)
}