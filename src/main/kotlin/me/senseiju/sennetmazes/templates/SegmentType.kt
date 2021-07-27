package me.senseiju.sennetmazes.templates

enum class SegmentType {
    STRAIGHT,
    CORNER,
    CROSSROADS,
    T,
    U,
    CONNECTOR;

    fun toSchemFileName() = "${toString().lowercase()}.schem"
}