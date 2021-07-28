package me.senseiju.sennetmazes.templates

import com.sk89q.worldedit.math.BlockVector3
import me.senseiju.sennetmazes.SennetMazes
import me.senseiju.sennetmazes.service.Service
import org.bukkit.Location
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
            val templateSection = templatesSection.getConfigurationSection(templateName) ?: return@forEach

            copyTemplatesFromResources(templateName)

            val playerSpawnOffset = Location(
                null,
                templateSection.getDouble("playerSpawnOffset.x"),
                templateSection.getDouble("playerSpawnOffset.y"),
                templateSection.getDouble("playerSpawnOffset.z")
            )

            templates[templateName] = Template(
                plugin,
                templateName,
                templateSection.getInt("segmentSize", 15),
                templateSection.getInt("connectorDepth", 0),
                playerSpawnOffset
            )
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