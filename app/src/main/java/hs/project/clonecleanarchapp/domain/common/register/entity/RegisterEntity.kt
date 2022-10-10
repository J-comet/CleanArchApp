package hs.project.clonecleanarchapp.domain.common.register.entity

data class RegisterEntity(
    var id: Int,
    var name: String,
    var email: String,
    var token: String
)