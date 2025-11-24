package com.sorsix.healthforum.model.exceptions

class UserDiseaseAssociationNotFoundException(userId: Long, diseaseId: Long) :
    RuntimeException("User with ID $userId does not have disease with ID $diseaseId")