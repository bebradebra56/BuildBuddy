package com.buildsof.budsde.data

import java.util.Date
import java.util.UUID

// Main Project Model
data class Project(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val address: String = "",
    val startDate: Date = Date(),
    val currency: Currency = Currency.USD,
    val photoUri: String? = null,
    val rooms: List<Room> = emptyList(),
    val budget: Double = 0.0,
    val notes: List<Note> = emptyList()
)

// Room/Zone Model
data class Room(
    val id: String = UUID.randomUUID().toString(),
    val type: RoomType,
    val name: String,
    val dimensions: Dimensions,
    val doors: Int = 0,
    val windows: Int = 0,
    val doorArea: Double = 2.0, // standard door area in m²
    val windowArea: Double = 1.5, // standard window area in m²
    val marginPercent: Int = 10,
    val workItems: List<WorkItem> = emptyList(),
    val calculateWalls: Boolean = true,
    val calculateFloor: Boolean = true,
    val calculateCeiling: Boolean = true
)

data class Dimensions(
    val length: Double = 0.0,
    val width: Double = 0.0,
    val height: Double = 0.0
) {
    val floorArea: Double get() = length * width
    val wallArea: Double get() = 2 * (length + width) * height
    val ceilingArea: Double get() = length * width
}

enum class RoomType {
    ROOM, KITCHEN, BATHROOM, HALLWAY, BALCONY, OTHER
}

// Work Item Models
data class WorkItem(
    val id: String = UUID.randomUUID().toString(),
    val type: WorkType,
    val enabled: Boolean = false,
    val config: WorkConfig? = null
)

enum class WorkType {
    PAINT_WALLS,
    WALLPAPER,
    WALL_TILES,
    FLOOR_TILES,
    LAMINATE,
    BASEBOARD,
    CEILING_PAINT,
    PRIMER,
    PUTTY
}

// Work Configurations
sealed class WorkConfig {
    data class PaintConfig(
        val paintType: PaintType = PaintType.MATTE,
        val coverage: Double = 10.0, // m² per liter
        val layers: Int = 2,
        val brand: String = "Generic",
        val pricePerLiter: Double = 15.0,
        val includeRoller: Boolean = true,
        val includeBrush: Boolean = true,
        val includeTray: Boolean = true
    ) : WorkConfig()

    data class WallpaperConfig(
        val type: WallpaperType = WallpaperType.VINYL,
        val rollWidth: Double = 0.53, // meters
        val rollLength: Double = 10.05, // meters
        val hasPattern: Boolean = false,
        val patternRepeat: Int = 0, // cm
        val glueType: GlueType = GlueType.UNIVERSAL,
        val pricePerRoll: Double = 25.0,
        val includeCorners: Boolean = false,
        val includeBaseboard: Boolean = false
    ) : WorkConfig()

    data class TileConfig(
        val surface: TileSurface = TileSurface.FLOOR,
        val tileWidth: Double = 30.0, // cm
        val tileHeight: Double = 30.0, // cm
        val layout: TileLayout = TileLayout.STRAIGHT,
        val margin: Int = 10,
        val glue: TileGlue = TileGlue(coverageKgPerM2 = 5.0, pricePerKg = 8.0),
        val grout: TileGrout = TileGrout(color = "#FFFFFF", pricePerKg = 10.0),
        val spacerSize: Int = 2, // mm
        val pricePerM2: Double = 35.0
    ) : WorkConfig()

    data class LaminateConfig(
        val type: FlooringType = FlooringType.LAMINATE,
        val classRating: Int = 32,
        val underlayment: Underlayment? = Underlayment(thickness = 2.0, pricePerM2 = 3.0),
        val layout: FloorLayout = FloorLayout.LENGTHWISE,
        val pricePerM2: Double = 30.0,
        val includeThreshold: Boolean = true,
        val includeBaseboard: Boolean = true
    ) : WorkConfig()
}

enum class PaintType { MATTE, SATIN, GLOSS }
enum class WallpaperType { FLEECE, VINYL, PAPER }
enum class GlueType { UNIVERSAL, FLEECE, VINYL }
enum class TileSurface { FLOOR, WALLS }
enum class TileLayout { STRAIGHT, DIAGONAL, HERRINGBONE }
enum class FlooringType { LAMINATE, SPC, LINOLEUM }
enum class FloorLayout { LENGTHWISE, CROSSWISE, DIAGONAL }

data class TileGlue(
    val coverageKgPerM2: Double,
    val pricePerKg: Double
)

data class TileGrout(
    val color: String,
    val pricePerKg: Double
)

data class Underlayment(
    val thickness: Double,
    val pricePerM2: Double
)

// Shopping List Models
data class ShoppingItem(
    val id: String = UUID.randomUUID().toString(),
    val category: ShoppingCategory,
    val name: String,
    val quantity: Double,
    val unit: String,
    val price: Double = 0.0,
    val isPurchased: Boolean = false,
    val alternatives: List<String> = emptyList()
)

enum class ShoppingCategory {
    MATERIALS, CONSUMABLES, TOOLS, MISCELLANEOUS
}

// Notes & Tasks
data class Note(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val tasks: List<Task> = emptyList(),
    val photos: List<String> = emptyList(),
    val createdAt: Date = Date()
)

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val priority: Priority = Priority.MEDIUM,
    val dueDate: Date? = null,
    val isCompleted: Boolean = false,
    val beforePhoto: String? = null,
    val afterPhoto: String? = null
)

enum class Priority { LOW, MEDIUM, HIGH }

// Template Model
data class Template(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val roomType: RoomType,
    val workItems: List<WorkItem>
)

// Currency
enum class Currency(val symbol: String) {
    USD("$"),
    EUR("€"),
    GBP("£")
}

// Settings
data class AppSettings(
    val units: MeasurementUnit = MeasurementUnit.METERS,
    val currency: Currency = Currency.USD,
    val defaultMargin: Int = 10,
    val isDarkTheme: Boolean = false
)

enum class MeasurementUnit {
    METERS, CENTIMETERS
}
