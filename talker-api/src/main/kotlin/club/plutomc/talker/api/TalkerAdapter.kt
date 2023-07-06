package club.plutomc.talker.api

interface TalkerAdapter<T> : TalkerSerializer<T>, TalkerDeserializer<T> {
}