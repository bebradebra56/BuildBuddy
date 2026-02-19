package com.buildsof.budsde.utils

import com.buildsof.budsde.data.*
import kotlin.math.ceil

object MaterialCalculator {
    
    fun calculateShoppingList(project: Project): List<ShoppingItem> {
        val items = mutableListOf<ShoppingItem>()
        
        project.rooms.forEach { room ->
            room.workItems.filter { it.enabled && it.config != null }.forEach { workItem ->
                when (val config = workItem.config) {
                    is WorkConfig.PaintConfig -> {
                        items.addAll(calculatePaintMaterials(room, config))
                    }
                    is WorkConfig.WallpaperConfig -> {
                        items.addAll(calculateWallpaperMaterials(room, config))
                    }
                    is WorkConfig.TileConfig -> {
                        items.addAll(calculateTileMaterials(room, config))
                    }
                    is WorkConfig.LaminateConfig -> {
                        items.addAll(calculateLaminateMaterials(room, config))
                    }
                    null -> {
                        // Simple work items without detailed config
                        items.add(ShoppingItem(
                            name = workItem.type.name.replace("_", " "),
                            quantity = 1.0,
                            unit = "item",
                            price = 0.0,
                            category = ShoppingCategory.MATERIALS
                        ))
                    }
                }
            }
        }
        
        return items
    }
    
    private fun calculatePaintMaterials(room: Room, config: WorkConfig.PaintConfig): List<ShoppingItem> {
        val items = mutableListOf<ShoppingItem>()
        val dims = room.dimensions
        
        // Calculate wall area
        val wallArea = if (room.calculateWalls) {
            2 * (dims.length + dims.width) * dims.height - 
            (room.doors * room.doorArea) - 
            (room.windows * room.windowArea)
        } else 0.0
        
        // Calculate ceiling area
        val ceilingArea = if (room.calculateCeiling) {
            dims.length * dims.width
        } else 0.0
        
        val totalArea = (wallArea + ceilingArea) * (1 + room.marginPercent / 100.0)
        
        // Calculate paint quantity
        val litersNeeded = (totalArea / config.coverage) * config.layers
        val litersWithMargin = ceil(litersNeeded)
        
        items.add(ShoppingItem(
            name = "${config.brand} ${config.paintType.name} Paint",
            quantity = litersWithMargin,
            unit = "L",
            price = config.pricePerLiter,
            category = ShoppingCategory.MATERIALS
        ))
        
        // Add tools if needed
        if (config.includeRoller) {
            items.add(ShoppingItem(
                name = "Paint Roller",
                quantity = 1.0,
                unit = "pc",
                price = 8.0,
                category = ShoppingCategory.TOOLS
            ))
        }
        
        if (config.includeBrush) {
            items.add(ShoppingItem(
                name = "Paint Brush",
                quantity = 1.0,
                unit = "pc",
                price = 5.0,
                category = ShoppingCategory.TOOLS
            ))
        }
        
        if (config.includeTray) {
            items.add(ShoppingItem(
                name = "Paint Tray",
                quantity = 1.0,
                unit = "pc",
                price = 3.0,
                category = ShoppingCategory.TOOLS
            ))
        }
        
        return items
    }
    
    private fun calculateWallpaperMaterials(room: Room, config: WorkConfig.WallpaperConfig): List<ShoppingItem> {
        val items = mutableListOf<ShoppingItem>()
        val dims = room.dimensions
        
        // Calculate wall area
        val wallArea = 2 * (dims.length + dims.width) * dims.height - 
                      (room.doors * room.doorArea) - 
                      (room.windows * room.windowArea)
        
        val areaWithMargin = wallArea * (1 + room.marginPercent / 100.0)
        
        // Calculate rolls needed
        val rollArea = config.rollWidth * config.rollLength
        val rollsNeeded = ceil(areaWithMargin / rollArea)
        
        items.add(ShoppingItem(
            name = "${config.type.name} Wallpaper",
            quantity = rollsNeeded,
            unit = "roll",
            price = config.pricePerRoll,
            category = ShoppingCategory.MATERIALS
        ))
        
        // Add glue
        items.add(ShoppingItem(
            name = "${config.glueType.name} Wallpaper Glue",
            quantity = ceil(areaWithMargin / 5.0), // ~5m² per package
            unit = "pkg",
            price = 10.0,
            category = ShoppingCategory.CONSUMABLES
        ))
        
        return items
    }
    
    private fun calculateTileMaterials(room: Room, config: WorkConfig.TileConfig): List<ShoppingItem> {
        val items = mutableListOf<ShoppingItem>()
        val dims = room.dimensions
        
        val area = when (config.surface) {
            TileSurface.FLOOR -> dims.length * dims.width
            TileSurface.WALLS -> 2 * (dims.length + dims.width) * dims.height
        }
        
        val areaWithMargin = area * (1 + config.margin / 100.0)
        
        // Calculate tiles needed
        val tileArea = (config.tileWidth / 100.0) * (config.tileHeight / 100.0)
        val tilesNeeded = ceil(areaWithMargin / tileArea)
        
        items.add(ShoppingItem(
            name = "Tiles ${config.tileWidth}x${config.tileHeight}cm",
            quantity = tilesNeeded,
            unit = "pc",
            price = config.pricePerM2 * tileArea,
            category = ShoppingCategory.MATERIALS
        ))
        
        // Add tile glue
        val glueKg = ceil(areaWithMargin * config.glue.coverageKgPerM2)
        items.add(ShoppingItem(
            name = "Tile Adhesive",
            quantity = glueKg,
            unit = "kg",
            price = config.glue.pricePerKg,
            category = ShoppingCategory.CONSUMABLES
        ))
        
        // Add grout
        val groutKg = ceil(areaWithMargin * 0.5) // ~0.5kg per m²
        items.add(ShoppingItem(
            name = "Tile Grout",
            quantity = groutKg,
            unit = "kg",
            price = config.grout.pricePerKg,
            category = ShoppingCategory.CONSUMABLES
        ))
        
        // Add spacers
        items.add(ShoppingItem(
            name = "Tile Spacers ${config.spacerSize}mm",
            quantity = 1.0,
            unit = "pkg",
            price = 5.0,
            category = ShoppingCategory.TOOLS
        ))
        
        return items
    }
    
    private fun calculateLaminateMaterials(room: Room, config: WorkConfig.LaminateConfig): List<ShoppingItem> {
        val items = mutableListOf<ShoppingItem>()
        val dims = room.dimensions
        
        val floorArea = dims.length * dims.width
        val areaWithMargin = floorArea * 1.1 // 10% margin for cuts
        
        items.add(ShoppingItem(
            name = "${config.type.name} Flooring Class ${config.classRating}",
            quantity = areaWithMargin,
            unit = "m²",
            price = config.pricePerM2,
            category = ShoppingCategory.MATERIALS
        ))
        
        // Add underlayment if specified
        if (config.underlayment != null) {
            items.add(ShoppingItem(
                name = "Underlayment ${config.underlayment.thickness}mm",
                quantity = areaWithMargin,
                unit = "m²",
                price = config.underlayment.pricePerM2,
                category = ShoppingCategory.MATERIALS
            ))
        }
        
        if (config.includeBaseboard) {
            val perimeter = 2 * (dims.length + dims.width)
            items.add(ShoppingItem(
                name = "Baseboard",
                quantity = perimeter,
                unit = "m",
                price = 8.0,
                category = ShoppingCategory.MATERIALS
            ))
        }
        
        if (config.includeThreshold) {
            items.add(ShoppingItem(
                name = "Door Threshold",
                quantity = room.doors.toDouble(),
                unit = "pc",
                price = 15.0,
                category = ShoppingCategory.MATERIALS
            ))
        }
        
        return items
    }
}
