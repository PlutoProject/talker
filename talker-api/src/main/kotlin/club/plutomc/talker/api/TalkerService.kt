package club.plutomc.talker.api

interface TalkerService {

    fun createPacket(serializationFunction: (TalkerWriter) -> Unit): TalkerPacket

    companion object {

        private val registeredServices: MutableMap<String, TalkerService> = mutableMapOf()

        init {
            // Trying to register default services if it's available
            this.tryToRegisterInternalService("simple", "club.plutomc.talker.simple.SimpleTalkerService")
            this.tryToRegisterInternalService("client", "club.plutomc.talker.simple.client.SimpleTalkerServiceClient")
            this.tryToRegisterInternalService("server", "club.plutomc.talker.simple.server.SimpleTalkerServiceServer")
        }

        @JvmStatic
        fun registerService(id: String, service: TalkerService) {
            if (this.registeredServices.containsKey(id))
                throw IllegalAccessException("The id of the service has been registered")
            if (this.registeredServices.containsValue(service))
                throw IllegalAccessException("The service has been registered")
            this.registeredServices[id] = service
        }

        @JvmStatic
        fun getService(id: String): TalkerService {
            if (!this.registeredServices.containsKey(id))
                throw IllegalAccessException("The id of the service not found")
            return this.registeredServices[id]!!
        }

        private fun tryToRegisterInternalService(serviceName: String, className: String) {
            try {
                val simpleTalkerService = Class.forName(className)
                this.registerService(serviceName, simpleTalkerService.kotlin.objectInstance as TalkerService)
            } catch (_: ClassNotFoundException) {
            }
        }

    }

}