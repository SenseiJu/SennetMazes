package me.senseiju.sennetmazes.service

class ServiceProvider {
    companion object {
        val services = hashSetOf<Service>()

        fun add(vararg servicesToAdd: Service) {
            servicesToAdd.forEach {
                services.add(it)
            }
        }

        inline fun <reified T : Service> get(): T {
            services.forEach {
                if (it is T) return it
            }

            throw NoServiceFoundException()
        }

        inline fun <reified T : Service> reload() {
            get<T>().onReload()
        }

        fun reloadAll() {
            services.forEach {
                it.onReload()
            }
        }

        inline fun <reified T : Service> disable() {
            get<T>().onDisable()
        }

        fun disableAll() {
            services.forEach {
                it.onDisable()
            }
        }
    }
}

class NoServiceFoundException : Exception() {
    override val message: String = "No service found for this type"
}