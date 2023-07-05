package club.plutomc.talker.minecraft

class SampleData {

    private val data: MutableMap<String, String> = mutableMapOf()

    fun add(key: String, value: String) {
        this.data[key] = value
    }

    fun contains(key: String): Boolean {
        return this.data.contains(key)
    }

    fun get(key: String): String {
        return this.data[key]!!
    }

    fun getAll(): Map<String, String> {
        return this.data.toMap()
    }

}