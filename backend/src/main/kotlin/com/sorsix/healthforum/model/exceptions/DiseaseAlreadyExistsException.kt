package com.sorsix.healthforum.model.exceptions

class DiseaseAlreadyExistsException(message: String) : RuntimeException(message) {

    companion object {
        fun byId(diseaseId: String) : DiseaseAlreadyExistsException =
            DiseaseAlreadyExistsException("Disease with ID '$diseaseId' already exists")

        fun byName(diseaseName: String) : DiseaseAlreadyExistsException =
            DiseaseAlreadyExistsException("Disease with name '$diseaseName' already exists")
    }
}