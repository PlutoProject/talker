package club.plutomc.talker.api.client

import club.plutomc.talker.api.TalkerService
import java.net.InetAddress

interface TalkerServiceClient: TalkerService {

    fun createClient(name: String, address: InetAddress, port: Int): TalkerClient

}