package me.senseiju.sennetmazes.exceptions

class NoServiceFoundException : Exception() {
    override val message: String = "No service found for this type"
}