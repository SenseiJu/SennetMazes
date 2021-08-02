package me.senseiju.sennetmazes.templates

enum class SegmentType {
    STRAIGHT,
    CORNER,
    CROSSROADS,
    T,
    U,
    CONNECTOR,
    EXIT;

    fun toSchemFileName() = "${toString().lowercase()}.schem"
}