package club.plutomc.talker.minecraft

import club.plutomc.talker.api.TalkerContext
import club.plutomc.talker.api.TalkerService
import com.google.gson.Gson
import com.google.gson.JsonObject

internal val GSON = Gson()

class SampleEvent(val context: TalkerContext, val data: SampleData) {

    fun send(data: SampleData) {
        context.send(TalkerService.getService("simple").createPacket { writer ->
            writer.writeByte(127)
            writer.writeByte(0)
            writer.writeByte(64)
            val jsonObject = JsonObject()
            for (entry in data.getAll().entries) {
                jsonObject.addProperty(entry.key, entry.value)
            }
            val message = GSON.toJson(jsonObject)
            writer.writeInt(message.length)
            writer.writeString(message)
        })
    }

}