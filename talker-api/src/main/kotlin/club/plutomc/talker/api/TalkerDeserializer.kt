package club.plutomc.talker.api

interface TalkerDeserializer<T> {

    fun deserialize(t: T?, writer: TalkerWriter)

}