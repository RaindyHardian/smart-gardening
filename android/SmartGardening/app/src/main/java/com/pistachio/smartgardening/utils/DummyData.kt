package com.pistachio.smartgardening.utils

import com.pistachio.smartgardening.data.entity.PlantEntity

object DummyData {
    fun generateDummyPlants(): List<PlantEntity> {

        val plants = ArrayList<PlantEntity>()

        plants.add(
            PlantEntity(
                0,
                "P_01",
                "Aglaonema",
                "Aglaonema Commutatum",
                "This plant has more than 30 species and the leaves are its uniqueness",
                "Tropical Rain Forest",
                "Place in areas with low radiation intensity and high humidity",
                listOf(),
                "Rp 20.000,00 - Rp 200.000,00",
                "-",
                "Here",
                "Now"
            )
        )

        plants.add(
            PlantEntity(
                0,
                "P_02",
                "Kuping Gajah",
                "Anthurium",
                "The main attraction of the anthurium is its beautiful, unique and varied leaf shape. The leaves are generally dark green with large, prominent leaf veins and bones. So that it makes the figure of this plant look sturdy but still exudes elegance when it comes to adulthood",
                "Tropical Rain Forest",
                "Place in areas with low radiation intensity and high humidity (do not put under direct sunlight)",
                listOf(),
                "Rp 25.000,00 - Rp 500.000,00",
                "-",
                "Here",
                "Now"
            )
        )

        plants.add(
            PlantEntity(
                0,
                "P_04",
                "Suplir",
                "Adiantum",
                "has a distinctive frond (ental) appearance",
                "Humid and wet except in snow areas and sheltered places such as mountain slopes.",
                "Can be interior and eksterior plant, This plant cannot withstand direct sunlight. Suplir is attracted to growing media that is fertile, rich in organic matter (humus), and is always moist, but is not tolerant of puddles. It likes fertilizing with a higher nitrogen content. Spore formation requires additional phosphorus and potassium.",
                listOf(),
                "Rp 30.000,00 - Rp 100.000,00",
                "-",
                "Here",
                "Now"
            )
        )
        return plants
    }
}