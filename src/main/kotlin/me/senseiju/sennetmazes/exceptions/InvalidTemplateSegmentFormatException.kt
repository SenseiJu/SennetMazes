package me.senseiju.sennetmazes.exceptions

import me.senseiju.sennetmazes.templates.SegmentType

class InvalidTemplateSegmentFormatException(template: String, segment: SegmentType) : Exception() {
    override val message: String = "Invalid format found for {$segment} segment from {$template} template"
}