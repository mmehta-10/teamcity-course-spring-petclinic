import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.VcsTrigger
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2020.2"

project {

    buildType(Build)

    features {
        feature {
            id = "PROJECT_EXT_2"
            type = "buildtype-graphs"
            param("series", """
                [
                  {
                    "type": "valueType",
                    "title": "Build Checkout Time",
                    "key": "buildStageDuration:sourcesUpdate"
                  },
                  {
                    "type": "valueType",
                    "title": "BuildTestStatus",
                    "key": "BuildTestStatus"
                  }
                ]
            """.trimIndent())
            param("format", "text")
            param("title", "New chart title")
            param("seriesTitle", "Serie")
        }
        feature {
            id = "PROJECT_EXT_3"
            type = "project-graphs"
            param("series", """
                [
                  {
                    "type": "valueType",
                    "title": "Time Spent in Queue",
                    "sourceBuildTypeId": "TeamcityCourseSpringPetclinic_Build",
                    "key": "TimeSpentInQueue"
                  },
                  {
                    "type": "valueType",
                    "title": "Artifacts Size",
                    "sourceBuildTypeId": "TeamcityCourseSpringPetclinic_Build",
                    "key": "VisibleArtifactsSize"
                  }
                ]
            """.trimIndent())
            param("format", "integer")
            param("hideFilters", "")
            param("title", "New chart title")
            param("defaultFilters", "")
            param("seriesTitle", "Serie")
        }
    }
}

object Build : BuildType({
    name = "Build"

    artifactRules = "target/petclinic.war"
    publishArtifacts = PublishMode.SUCCESSFUL

    vcs {
        root(DslContext.settingsRoot)

        showDependenciesChanges = true
    }

    steps {
        maven {
            name = "build-test"
            goals = "clean package"
        }
    }

    triggers {
        vcs {
            quietPeriodMode = VcsTrigger.QuietPeriodMode.USE_CUSTOM
            quietPeriod = 5
            triggerRules = "+:."
            branchFilter = ""
            perCheckinTriggering = true
            groupCheckinsByCommitter = true
            enableQueueOptimization = false
        }
    }
})
