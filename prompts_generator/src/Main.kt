import java.net.HttpURLConnection
import java.net.URI
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

// Подстановка аргументов в шаблонную строку

fun templatedStringInterpolation(templatedString: String, arguments: Map<String, Any>): String {
    var result = templatedString
    arguments.forEach{
        result = result.replace("[${it.key}]", it.value.toString())
    }
    if (result.findAnyOf(listOf("[", "]")) != null) {
        throw IllegalArgumentException()
    }
    return result
}

private val url: URL = URI.create("https://api-inference.huggingface.co/models/google/flan-t5-large").toURL()
// Отправка серии запросов
fun sendPrompts(prompts: List<String>): Map<Int, String> {
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "POST"
    connection.setRequestProperty("Content-Type", "application/json")
    connection.setRequestProperty("Authorization", "Bearer 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")
    connection.doOutput = true

    val jsonPrompts = JSONObject().apply { put("inputs", JSONArray(prompts))
                                           put("parameters", JSONObject().apply { put("max_length", 500) })
                                         }.toString().toByteArray()
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
fun generatePrompts(seriesOfPrompts: SeriesOfPrompts): Map<Int, String> {
    val prompts = seriesOfPrompts.arguments.map { templatedStringInterpolation(seriesOfPrompts.templatedPrompt, it) }
    return sendPrompts(prompts)
}

// Серия запросов после парсинга
data class SeriesOfPrompts(val templatedPrompt: String, val arguments: List<Map<String, Any>>)

// Обработка входных данных
fun parseInput(): SeriesOfPrompts {
    val templatedPrompt: String = buildString {
        var line = readln()
        while (line.trim() != "") {
            append(line)
            line = readln()
        }
    }
    val promptsCount: Int = readln().trim().toInt()
    val arguments: List<MutableMap<String, String>> = List(promptsCount) { mutableMapOf() }
    for (i in 0..< promptsCount) {
        for (keyValue in readln().trim().split(", ", ",", " , ", " ,")) {
            val keyValueList = keyValue.split("=", " = ")
            require(keyValueList.size == 2)
            arguments[i][keyValueList[0]] = keyValueList[1]
        }
    }
    return SeriesOfPrompts(templatedPrompt, arguments)
}

// Красивый вывод
fun printResponse(respond: Map<Int, String>): Unit {
    for ((key, value) in respond) {
        println("$key : $value")
    }
}

fun main() {
    val seriesOfPrompts = parseInput()
    val respond = generatePrompts(seriesOfPrompts)
    printResponse(respond)
}