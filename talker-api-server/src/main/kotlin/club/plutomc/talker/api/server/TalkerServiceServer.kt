package club.plutomc.talker.api.server

import club.plutomc.talker.api.TalkerService

interface TalkerServiceServer: TalkerService {

    fun createServer(port: Int): TalkerServer

}