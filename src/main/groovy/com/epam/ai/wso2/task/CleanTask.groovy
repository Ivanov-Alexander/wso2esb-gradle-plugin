package com.epam.ai.wso2.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import static com.epam.ai.wso2.global.Constant.TARGET

class CleanTask extends DefaultTask {
	@TaskAction
	void clean() {
		project.file(TARGET).deleteDir()
	}
}
