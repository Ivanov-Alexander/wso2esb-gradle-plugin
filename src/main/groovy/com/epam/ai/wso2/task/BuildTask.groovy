package com.epam.ai.wso2.task

import com.epam.ai.wso2.global.Utils
import groovy.xml.XmlUtil
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.TaskAction

import java.nio.file.Files

import static com.epam.ai.wso2.global.Constant.*

class BuildTask extends DefaultTask {
	protected Set<String> dependencies = new LinkedHashSet<>()
	protected XmlParser parser = new XmlParser(false, false)

	BuildTask() {
		inputs.sourceDir(SYNAPSE_CONFIG)
		outputs.file("$TARGET/$TEMP")
	}

	@TaskAction
	void build() {
		project.file(TARGET).deleteDir()
		project.file("$TARGET/$TEMP").mkdirs()

		buildApi()
		buildMessageStores()//must be before message-processors
		buildMessageProcessors()
		buildTemplates()
		buildSynapseConfigurations()

		populateGlobalArtifact()
	}

	private void buildApi() {
		copyBuild('api')
	}

	private void buildMessageProcessors() {
		copyBuild('message-processors')
	}

	private void buildMessageStores() {
		copyBuild('message-stores')
	}

	private void buildTemplates() {
		copyBuild('templates')
	}


	private void copyBuild(folder) {
		project.fileTree(dir: "$SYNAPSE_CONFIG/$folder").each { file ->
			String artifactName = Utils.fileName(file)

			File artifactFolder = createArtifactFolder(artifactName)
			Files.copy(file.toPath(), artifactFolder.toPath().resolve(file.toPath().getFileName()))

			createArtifactInfo(artifactName, Utils.FOLDER_TO_ARTIFACT_TYPE_MAPPING.get(folder), artifactFolder)

			dependencies.add(artifactName)
		}
	}

	private void buildSynapseConfigurations() {
		FileTree sourceTree = project.fileTree(dir: SYNAPSE_CONFIG)
		sourceTree.exclude('api', 'endpoints', 'local-entries', 'message-processors', 'message-stores', 'proxy-services', 'sequences', 'tasks', 'templates')

		sourceTree.each { file ->
			Node root = parser.parse(file)

			List<Node> nodes = root.children()
			nodes.each { node ->
				String nodeType = node.name()
				String artifactName

				switch (nodeType) {
					case 'registry':
						return
					case "localEntry":
						artifactName = node.@key
						break
					default:
						artifactName = node.@name
				}

				File artifactFolder = createArtifactFolder(artifactName)

				File artifact = project.file("$TARGET/$TEMP/$artifactFolder.name/$artifactName" + XML_EXT)
				artifact.createNewFile()

				node.@xmlns = 'http://ws.apache.org/ns/synapse'

				artifact.write(XmlUtil.serialize(node))

				createArtifactInfo(artifactName, Utils.ELEMENT_TO_ARTIFACT_TYPE_MAPPING.get(nodeType), artifactFolder)
				dependencies.add(artifactName)
			}
		}
	}

	private void populateGlobalArtifact() {
		File artifacts = project.file("$TARGET/$TEMP/$ARTIFACTS")
		artifacts.createNewFile()
		artifacts.write(Utils.generateProjectArtifact(project.name, project.version.toString()))
		Node root = new XmlParser().parse(artifacts)
		dependencies.each {
			Node dependency = new XmlParser().parseText(Utils.generateDependency(it, project.version.toString()))
			root.artifact[0].append(dependency)
		}
		artifacts.write(XmlUtil.serialize(root))
	}

	protected File createArtifactFolder(String artifactName) {
		String artifactFolderName = artifactName + "_" + project.version
		File artifactFolder = project.file("$TARGET/$TEMP/$artifactFolderName")
		artifactFolder.mkdirs()

		return artifactFolder
	}

	protected File createArtifactInfo(String artifactName, String artifactType, File artifactFolder) {
		File artifactInfo = project.file("$TARGET/$TEMP/$artifactFolder.name/$ARTIFACT")
		artifactInfo.createNewFile()
		artifactInfo.write(Utils.generateArtifact(artifactName, artifactType, project.version.toString()))

		return artifactInfo
	}
}


