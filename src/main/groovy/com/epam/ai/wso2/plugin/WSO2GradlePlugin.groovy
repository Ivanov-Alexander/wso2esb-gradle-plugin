package com.epam.ai.wso2.plugin

import com.epam.ai.wso2.configuration.Configuration
import com.epam.ai.wso2.task.BuildTask
import com.epam.ai.wso2.task.CarTask
import com.epam.ai.wso2.task.CleanTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Copy

class WSO2GradlePlugin implements Plugin<Project> {
	@Override
	void apply(Project project) {
		checkConfigurations(project)

		Task build = project.task('build', type: BuildTask)

		Task car = project.task('car', type: CarTask)
		car.dependsOn { build }

		if (project.hasProperty(Configuration.Property.DEPLOY_LOCATION)) {
			Copy deploy = project.task('deploy', type: Copy) as Copy
			deploy.from('target') {
				include '**/*.car'
				exclude 'temp'
			}
			deploy.into project.property(Configuration.Property.DEPLOY_LOCATION)
			deploy.dependsOn { car }
		}

		project.task('clean', type: CleanTask)

	}

	private static void checkConfigurations(Project project) {
		if (!project.hasProperty(Configuration.Property.CONFIGURATION)) {
			project.logger.warn('[WARN] Configuration has not been found. Processing will be skipped!')
		}
		if (!project.hasProperty(Configuration.Property.DEPLOY_LOCATION)) {
			project.logger.warn('[WARN] Deploy location has not been found. Task will not be created!')
		}
	}
}