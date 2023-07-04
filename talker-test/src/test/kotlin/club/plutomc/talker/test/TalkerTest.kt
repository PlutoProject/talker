package club.plutomc.talker.test

import club.plutomc.talker.api.TalkerService

class TalkerTest {

    fun test1(talkerService: TalkerService) {
        val packet = talkerService.createPacket { writer ->
            writer.writeByte(1)
            writer.writeInt(113)
            writer.writeString("Hello, world!")
        }
    }

}