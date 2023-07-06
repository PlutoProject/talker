package club.plutomc.talker.minecraft

import club.plutomc.talker.api.TalkerContext
import club.plutomc.talker.api.TalkerDataReceiver
import club.plutomc.talker.api.TalkerManager
import club.plutomc.talker.api.TalkerPacket
import club.plutomc.talker.simple.SimpleTalkerReader
import club.plutomc.talker.simple.SimpleTalkerWriter
import com.google.gson.JsonParser

abstract class SampleReceiver(val provider: SampleListenerProvider) : TalkerDataReceiver {

    override fun receive(manager: TalkerManager, context: TalkerContext, rawPacket: TalkerPacket, packet: Any) {
        val writer = SimpleTalkerWriter()
        rawPacket.write(writer)
        val reader = SimpleTalkerReader(writer.toByteArray())
        if (reader.availableLength() < 7)
            return
        if (reader.readByte() != 127.toByte())
            return
        if (reader.readByte() != 0.toByte())
            return
        if (reader.readByte() != 64.toByte())
            return
        val length = reader.readInt()
        val rawJson = reader.readString(length)
        val jsonObject = JsonParser.parseString(rawJson).asJsonObject
        val sampleData = SampleData()
        for (key in jsonObject.keySet()) {
            sampleData.add(key, jsonObject[key].asString)
        }
        val event = SampleEvent(context, sampleData)
        for (listener in provider.getListeners()) {
            this.receive0(listener, event)
        }
    }

    abstract fun receive0(listener: SampleListener, event: SampleEvent)

    override fun handleException(exception: Exception) {
        throw exception
    }

}