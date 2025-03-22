import java.net.HttpURLConnection
import java.net.URI
import org.json.JSONArray
import org.json.JSONObject

// Шаблонная строка
class TemplatedString (val templatedString: String, val arguments: Map<String, Any>) {
    override fun toString(): String {
        var result = templatedString
        arguments.forEach{
            result = result.replace("[${it.key}]", it.value.toString())
        }

        if (result.findAnyOf(listOf("[", "]")) != null) {
            throw IllegalArgumentException()
        }

        return result
    }
}

// Отправка серии запросов
fun sendPrompts(prompts: List<String>): Map<Int, String> {
    val url = URI.create("https://api-inference.huggingface.co/models/google/flan-t5-large").toURL()
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "POST"
    connection.setRequestProperty("Content-Type", "application/json")
    connection.setRequestProperty("Authorization", "Bearer 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")
    connection.doOutput = true

    val jsonPrompts = JSONObject().apply { put("inputs", JSONArray(prompts)) }.toString().toByteArray()
    connection.outputStream.use { os -> os.write(jsonPrompts) }

    val responseCode = connection.responseCode
    if (responseCode == HttpURLConnection.HTTP_OK) {
        val jsonRespond = JSONArray(connection.inputStream.bufferedReader().use { it.readText() })
        return (1..prompts.size).associateWith { jsonRespond.getJSONObject(it - 1).get("generated_text").toString()}
    } else {
        val errorResponse = connection.errorStream.bufferedReader().use { it.readText() }
        throw Exception("Ошибка при запросе к API: $responseCode, $errorResponse")
    }

}

// Генерация запросов
fun generatePrompts(templatedPrompt: String, arguments: List<Map<String, Any>>): Map<Int, String> {
    val prompts = arguments.map { TemplatedString(templatedPrompt, it).toString() }
    return sendPrompts(prompts)
}

// Обработка входных данных
fun parseInput(): Pair<String,  List<Map<String, String>>> {
    var templatedPrompt: String = String()
    val arguments: MutableList<MutableMap<String, String>> = mutableListOf()
    var line = readln()
    while (line.trim() != "") {
        templatedPrompt += line
        line = readln()
    }
    val promptsCount: Int = readln().trim().toInt()
    var keyValueList: List<String>
    for (i in 0..< promptsCount) {
        arguments += mutableMapOf()
        for (keyValue in  readln().trim().split(", ", ",", " , ", " ,")) {
            keyValueList = keyValue.split("=", " = ")
            if (keyValueList.size != 2) {
                throw IllegalArgumentException()
            }
            arguments[i] += Pair(keyValueList[0], keyValueList[1])
        }
    }
    return Pair(templatedPrompt, arguments)
}

// Красивый вывод
fun printRespond(respond: Map<Int, String>): Unit {
    for ((key, value) in respond) {
        println("$key : $value")
    }
}

fun main() {
    val (templatedString, arguments)  = parseInput()
    val respond = generatePrompts(templatedString, arguments)
    printRespond(respond)
}