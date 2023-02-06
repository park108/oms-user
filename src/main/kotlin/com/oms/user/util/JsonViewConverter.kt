package com.oms.user.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import org.springframework.stereotype.Component

interface JsonViewConverter {
	fun convert(obj: ByteArray): String
}

@Component
class NormalConverter: JsonViewConverter {
	override fun convert(obj: ByteArray): String {
		return String(obj, Charsets.UTF_8)
	}
}

@Component
class PrettyConverter : JsonViewConverter {

	private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

	override fun convert(obj: ByteArray): String {
		return gson.toJson(JsonParser.parseString(String(obj, Charsets.UTF_8)))
	}
}