package com.epam.ai.wso2.task

import com.epam.ai.wso2.configuration.Configuration
import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.api.tasks.bundling.Zip

import static com.epam.ai.wso2.global.Constant.*

class CarTask extends Zip {
	CarTask() {
		extension = 'car'
		baseName = project.name
		destinationDir = project.file(TARGET)
		from(project.file("$TARGET/$TEMP"))
		if (project.hasProperty(Configuration.Property.CONFIGURATION)) {
			filter(ReplaceTokens, tokens: project.property(Configuration.Property.CONFIGURATION))
		}
		inputs.sourceDir("$TARGET/$TEMP")
		outputs.file("$TARGET/$project.name" + CAR_EXT)
	}
}
