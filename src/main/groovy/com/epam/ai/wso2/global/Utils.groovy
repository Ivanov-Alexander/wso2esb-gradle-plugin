package com.epam.ai.wso2.global

class Utils {
	static final Map<String, String> ELEMENT_TO_ARTIFACT_TYPE_MAPPING = ['api'       : 'api',
	                                                                     'localEntry': 'local-entry',
	                                                                     'sequence'  : 'sequence',
	                                                                     'proxy'     : 'proxy-service',
	                                                                     'endpoint'  : 'endpoint']


	static final Map<String, String> FOLDER_TO_ARTIFACT_TYPE_MAPPING = ['api'               : 'api',
	                                                                    'message-stores'    : 'message-store',
	                                                                    'message-processors': 'message-processors',
	                                                                    'templates'         : 'template']

	static String generateArtifact(String name, String type, String version) {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><artifact name=\"$name\" version=\"$version\" type=\"synapse/$type\" serverRole=\"EnterpriseServiceBus\">\n\t<file>$name" + ".xml</file>\n</artifact>"
	}

	static String generateProjectArtifact(String projectName, String version) {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><artifacts><artifact name=\"$projectName\" type=\"carbon/application\" version=\"$version\"></artifact></artifacts>"
	}

	static String generateDependency(String artifactName, String version) {
		return "<dependency artifact=\"$artifactName\" version=\"$version\" include=\"true\" serverRole=\"EnterpriseServiceBus\"/>"
	}

	static String fileName(File file) {
		return file.name.replaceFirst(~/\.[^\.]+$/, '')
	}
}
