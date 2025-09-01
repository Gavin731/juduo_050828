pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        maven {
            url = uri("https://maven.aliyun.com/repository/public")
        }
        maven {
            url = uri("https://artifact.bytedance.com/repository/AwemeOpenSDK")
        }
        maven {
            url =
                uri("https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_support/")
        }
        maven {
            url = uri("https://artifact.bytedance.com/repository/pangle")
        }
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
        maven {
            url = uri("https://maven.aliyun.com/repository/publi")
        }
        maven {
            url = uri("https://artifact.bytedance.com/repository/AwemeOpenSDK")
        }
        maven {
            url =
                uri("https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_support/")
        }
        maven {
            url = uri("https://artifact.bytedance.com/repository/pangle")
        }
    }
}

rootProject.name = "Video"
include(":app")
 