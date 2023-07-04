package club.plutomc.talker.api

interface TalkerSerializer<T> {

    fun serialize(reader: TalkerReader): T?
}