package me.senseiju.sennetmazes.templates

import me.senseiju.sennetmazes.SennetMazes
import me.senseiju.sennetmazes.extensions.isNumber
import me.senseiju.sennetmazes.service.Service
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import java.io.File
import java.util.jar.JarFile

class TemplateService(private val plugin: SennetMazes) : Service() {
    private val templates = hashMapOf<String, Template>()

    init {
        loadTemplates()
    }

    override fun onDisable() {

    }

    override fun onReload() {
        templates.clear()

        loadTemplates()
    }

    fun getTemplate(string: String) = templates[string]

    private fun loadTemplates() {
        val templatesSection = plugin.config.getConfigurationSection("templates") ?: return

        templatesSection.getKeys(false).forEach { templateName ->
            with (templatesSection.getConfigurationSection(templateName)) {
                if (this == null || !checkConfig(this)) {
                    println("ERROR: Could not load template '$templateName' because of invalid config")
                    return@forEach
                }

                copyTemplatesFromResources(templateName)

                val playerSpawnOffset = Location(
                    null,
                    getDouble("playerSpawnOffset.x"),
                    getDouble("playerSpawnOffset.y"),
                    getDouble("playerSpawnOffset.z")
                )

                try {
                    val template = Template(
                        plugin,
                        templateName,
                        getInt("segmentSize"),
                        getInt("connectorDepth"),
                        getInt("exitSegmentSize"),
                        playerSpawnOffset
                    )

                    templates[templateName] = template
                } catch (e: Exception) {
                    println("ERROR: Could not load template '$templateName' because of missing segment schematics")
                }
            }
        }
    }

    private fun checkConfig(section: ConfigurationSection): Boolean {
        return with (section) {
            isInt("segmentSize")
                    && isInt("exitSegmentSize")
                    && isNumber("playerSpawnOffset.x")
                    && isNumber("playerSpawnOffset.y")
                    && isNumber("playerSpawnOffset.z")
        }
    }

    private fun copyTemplatesFromResources(templateName: String) {
        val jar = JarFile(plugin.javaClass.protectionDomain.codeSource.location.toURI().path)
        val entries = jar.entries()

        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            if (!entry.name.equals("templates/$templateName/")) {
                continue
            }

            while (entries.hasMoreElements()) {
                val schematic = entries.nextElement()
                if (!schematic.name.contains("templates/$templateName")) {
                    return
                }

                if (!File(plugin.dataFolder, schematic.name).exists()) {
                    plugin.saveResource(schematic.name, false)
                }
            }
        }
    }
}