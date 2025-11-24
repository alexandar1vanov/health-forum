package com.sorsix.healthforum.model.exceptions

class DiseaseNotFoundException(message: String) : RuntimeException(message) {

    companion object {
        fun byId(diseaseId: String): DiseaseNotFoundException =
            DiseaseNotFoundException("Disease with ID $diseaseId not found")

        fun byName(diseaseName: String): DiseaseNotFoundException =
            DiseaseNotFoundException("Disease with name $diseaseName not found")
    }
}