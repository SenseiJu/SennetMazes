package me.senseiju.sennetmazes.service

abstract class Service {
    open fun onDisable() {}
    open fun onReload() {}
}