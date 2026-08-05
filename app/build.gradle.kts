plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.tommy.gimnasio"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.tommy.gimnasio"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    sourceSets {
        getByName("main") {
            assets.srcDirs("src/main/assets", "../db")
            res.srcDirs(
                "src/main/res",
                "src/main/res-auth",
                "src/main/res-clients",
                "src/main/res-memberships",
                "src/main/res-users",
                "src/main/res-main",
                "src/main/res-payments",
                "src/main/res-attendance",
                "src/main/res-routines"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}