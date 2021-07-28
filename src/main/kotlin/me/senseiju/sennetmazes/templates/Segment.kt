package me.senseiju.sennetmazes.templates

import me.senseiju.sennetmazes.Cardinal

data class Segment(val segmentType: SegmentType, val rotation: Double, val openEdges: Set<Cardinal>)