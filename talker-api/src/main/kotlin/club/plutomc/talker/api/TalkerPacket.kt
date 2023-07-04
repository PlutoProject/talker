package club.plutomc.talker.api

interface TalkerPacket {

    fun write(writer: TalkerWriter)

}