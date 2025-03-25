import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.gmail.umidsafarov.lifebalance"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.gmail.umidsafarov.lifebalance"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.gmail.umidsafarov.lifebalance.HiltTestRunner"
    }

    val keystorePropertiesFile = rootProject.file("key.properties")
    val keystoreProperties = Properties()
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))

    if (keystoreProperties["key.alias"] != null) {
        //we have configured key.properties
        signingConfigs {
            create("release") {
                keyAlias = keystoreProperties["key.alias"] as String
                keyPassword = keystoreProperties["key.alias.password"] as String
                storeFile = file(keystoreProperties["key.store"] as String)
                storePassword = keystoreProperties["key.store.passord"] as String
            }
        }
    }


    val configPropertiesFile = rootProject.file("config.properties")
    val configProperties = Properties()
    configProperties.load(FileInputStream(configPropertiesFile))

    buildTypes {
        debug {
            isMinifyEnabled = false
            buildConfigField(
                "String",
                "DATABASE_NAME",
                configProperties["debugDatabaseName"] as String
            )
            buildConfigField("String", "API_URL", configProperties["debugApiEndpoint"] as String)
        }
        release {
            isMinifyEnabled = false
            buildConfigField(
                "String",
                "DATABASE_NAME",
                configProperties["releaseDatabaseName"] as String
            )
            buildConfigField("String", "API_URL", configProperties["releaseApiEndpoint"] as String)
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
        buildConfig = true
        compose = true
    }
}

dependencies {

    //core
    implementation(libs.androidx.core.ktx)
    implementation(libs.hilt)
    ksp(libs.hilt.compiler)
    implementation(libs.serialization)

    //data
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.androidx.room)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    annotationProcessor(libs.androidx.room.compiler)

    //ui
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.navigation)
    implementation(libs.androidx.compose.hilt.navigation)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.coil.compose)
    implementation(libs.coil.network)

    //tests
    testImplementation(libs.junit)
    testImplementation(libs.mockito)
    testImplementation(libs.mockito.ktx)
    testImplementation(libs.truth)
    testImplementation(libs.coroutines.testing)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.navigation.test)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.mockito)
    androidTestImplementation(libs.mockito.ktx)
    androidTestImplementation(libs.okhttp.testing)
    androidTestImplementation(libs.androidx.room.testing)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.android.compiler)
    androidTestImplementation(libs.truth)

    //debug
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}