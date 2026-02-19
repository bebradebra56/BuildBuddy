package com.buildsof.budsde.data.room

import com.google.gson.*
import com.buildsof.budsde.data.*
import java.lang.reflect.Type

class WorkConfigTypeAdapter : JsonSerializer<WorkConfig>, JsonDeserializer<WorkConfig> {
    
    override fun serialize(src: WorkConfig, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()
        
        when (src) {
            is WorkConfig.PaintConfig -> {
                jsonObject.addProperty("type", "PaintConfig")
                jsonObject.addProperty("paintType", src.paintType.name)
                jsonObject.addProperty("coverage", src.coverage)
                jsonObject.addProperty("layers", src.layers)
                jsonObject.addProperty("brand", src.brand)
                jsonObject.addProperty("pricePerLiter", src.pricePerLiter)
                jsonObject.addProperty("includeRoller", src.includeRoller)
                jsonObject.addProperty("includeBrush", src.includeBrush)
                jsonObject.addProperty("includeTray", src.includeTray)
            }
            is WorkConfig.WallpaperConfig -> {
                jsonObject.addProperty("type", "WallpaperConfig")
                jsonObject.addProperty("wallpaperType", src.type.name)
                jsonObject.addProperty("rollWidth", src.rollWidth)
                jsonObject.addProperty("rollLength", src.rollLength)
                jsonObject.addProperty("hasPattern", src.hasPattern)
                jsonObject.addProperty("patternRepeat", src.patternRepeat)
                jsonObject.addProperty("glueType", src.glueType.name)
                jsonObject.addProperty("pricePerRoll", src.pricePerRoll)
                jsonObject.addProperty("includeCorners", src.includeCorners)
            }
            is WorkConfig.TileConfig -> {
                jsonObject.addProperty("type", "TileConfig")
                jsonObject.addProperty("surface", src.surface.name)
                jsonObject.addProperty("tileWidth", src.tileWidth)
                jsonObject.addProperty("tileHeight", src.tileHeight)
                jsonObject.addProperty("layout", src.layout.name)
                jsonObject.addProperty("margin", src.margin)
                jsonObject.addProperty("pricePerM2", src.pricePerM2)
                jsonObject.addProperty("spacerSize", src.spacerSize)
                
                // Serialize glue
                val glueObj = JsonObject()
                glueObj.addProperty("coverageKgPerM2", src.glue.coverageKgPerM2)
                glueObj.addProperty("pricePerKg", src.glue.pricePerKg)
                jsonObject.add("glue", glueObj)
                
                // Serialize grout
                val groutObj = JsonObject()
                groutObj.addProperty("color", src.grout.color)
                groutObj.addProperty("pricePerKg", src.grout.pricePerKg)
                jsonObject.add("grout", groutObj)
            }
            is WorkConfig.LaminateConfig -> {
                jsonObject.addProperty("type", "LaminateConfig")
                jsonObject.addProperty("flooringType", src.type.name)
                jsonObject.addProperty("classRating", src.classRating)
                jsonObject.addProperty("layout", src.layout.name)
                jsonObject.addProperty("pricePerM2", src.pricePerM2)
                jsonObject.addProperty("includeThreshold", src.includeThreshold)
                jsonObject.addProperty("includeBaseboard", src.includeBaseboard)
                
                if (src.underlayment != null) {
                    val underlayment = JsonObject()
                    underlayment.addProperty("thickness", src.underlayment.thickness)
                    underlayment.addProperty("pricePerM2", src.underlayment.pricePerM2)
                    jsonObject.add("underlayment", underlayment)
                }
            }
        }
        
        return jsonObject
    }
    
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): WorkConfig {
        val jsonObject = json.asJsonObject
        val type = jsonObject.get("type").asString
        
        return when (type) {
            "PaintConfig" -> WorkConfig.PaintConfig(
                paintType = PaintType.valueOf(jsonObject.get("paintType").asString),
                coverage = jsonObject.get("coverage").asDouble,
                layers = jsonObject.get("layers").asInt,
                brand = jsonObject.get("brand")?.asString ?: "Generic",
                pricePerLiter = jsonObject.get("pricePerLiter")?.asDouble ?: 15.0,
                includeRoller = jsonObject.get("includeRoller")?.asBoolean ?: true,
                includeBrush = jsonObject.get("includeBrush")?.asBoolean ?: true,
                includeTray = jsonObject.get("includeTray")?.asBoolean ?: true
            )
            "WallpaperConfig" -> WorkConfig.WallpaperConfig(
                type = WallpaperType.valueOf(jsonObject.get("wallpaperType").asString),
                rollWidth = jsonObject.get("rollWidth").asDouble,
                rollLength = jsonObject.get("rollLength").asDouble,
                hasPattern = jsonObject.get("hasPattern")?.asBoolean ?: false,
                patternRepeat = jsonObject.get("patternRepeat")?.asInt ?: 0,
                glueType = GlueType.valueOf(jsonObject.get("glueType")?.asString ?: "UNIVERSAL"),
                pricePerRoll = jsonObject.get("pricePerRoll")?.asDouble ?: 25.0,
                includeCorners = jsonObject.get("includeCorners")?.asBoolean ?: false
            )
            "TileConfig" -> {
                val glueObj = jsonObject.getAsJsonObject("glue")
                val glue = TileGlue(
                    coverageKgPerM2 = glueObj.get("coverageKgPerM2").asDouble,
                    pricePerKg = glueObj.get("pricePerKg").asDouble
                )
                
                val groutObj = jsonObject.getAsJsonObject("grout")
                val grout = TileGrout(
                    color = groutObj.get("color").asString,
                    pricePerKg = groutObj.get("pricePerKg").asDouble
                )
                
                WorkConfig.TileConfig(
                    surface = TileSurface.valueOf(jsonObject.get("surface").asString),
                    tileWidth = jsonObject.get("tileWidth").asDouble,
                    tileHeight = jsonObject.get("tileHeight").asDouble,
                    layout = TileLayout.valueOf(jsonObject.get("layout")?.asString ?: "STRAIGHT"),
                    margin = jsonObject.get("margin")?.asInt ?: 10,
                    glue = glue,
                    grout = grout,
                    spacerSize = jsonObject.get("spacerSize")?.asInt ?: 2,
                    pricePerM2 = jsonObject.get("pricePerM2")?.asDouble ?: 0.0
                )
            }
            "LaminateConfig" -> {
                val underlaymentJson = jsonObject.get("underlayment")
                val underlayment = if (underlaymentJson != null && !underlaymentJson.isJsonNull) {
                    val obj = underlaymentJson.asJsonObject
                    Underlayment(
                        thickness = obj.get("thickness").asDouble,
                        pricePerM2 = obj.get("pricePerM2").asDouble
                    )
                } else null
                
                WorkConfig.LaminateConfig(
                    type = FlooringType.valueOf(jsonObject.get("flooringType").asString),
                    classRating = jsonObject.get("classRating").asInt,
                    underlayment = underlayment,
                    layout = FloorLayout.valueOf(jsonObject.get("layout")?.asString ?: "LENGTHWISE"),
                    pricePerM2 = jsonObject.get("pricePerM2")?.asDouble ?: 30.0,
                    includeThreshold = jsonObject.get("includeThreshold")?.asBoolean ?: true,
                    includeBaseboard = jsonObject.get("includeBaseboard")?.asBoolean ?: true
                )
            }
            else -> throw JsonParseException("Unknown WorkConfig type: $type")
        }
    }
}
