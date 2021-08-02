package me.senseiju.sennetmazes.extensions

import org.bukkit.configuration.ConfigurationSection

fun ConfigurationSection.isNumber(path: String) = isDouble(path) || isInt(path)