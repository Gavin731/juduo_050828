plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "com.film.television"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.film.television"
        minSdk = 24
        targetSdk = 35
        versionCode = 3
        versionName = "1.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    signingConfigs {
        create("config") {
            storeFile = File("/Users/zenglinggui/Documents/ad/juduo.jks")
            storePassword = "juduo123"
            keyAlias = "juduo"
            keyPassword = "juduo123"
        }
    }
    buildTypes {
        debug {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("config")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isZipAlignEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("config")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(fileTree("include" to arrayOf("*.jar", "*.aar"), "dir" to "libs"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.constrain)
    implementation(libs.androidx.viewpager2)
    implementation(libs.androidx.viewmodel)
    implementation(libs.androidx.livedata)
    implementation(libs.androidx.viewmodel.savedstate)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.paging)

    implementation(libs.flexbox)
    implementation(libs.gson)

    api(libs.retrofit)
    implementation(libs.gson.converter)
    implementation(libs.glide)
    ksp(libs.glide.ksp)

    // 友盟
    implementation("com.umeng.umsdk:common:+")
    implementation("com.umeng.umsdk:asms:+")
    implementation("com.umeng.umsdk:apm:+")

    // 广告sdk
    implementation("com.orhanobut:hawk:2.0.1")
    implementation("io.reactivex.rxjava3:rxjava:3.0.4")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:3.9.0")
    implementation("com.android.support:support-v4:28.0.0")
}