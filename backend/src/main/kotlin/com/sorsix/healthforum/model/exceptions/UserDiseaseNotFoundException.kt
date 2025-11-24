package com.sorsix.healthforum.model.exceptions

class UserDiseaseNotFoundException(id: Long) :
    RuntimeException("UserDisease not found with ID $id")