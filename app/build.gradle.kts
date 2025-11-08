plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.stpauls.dailyliturgy"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 23
        targetSdk = 35
        versionCode = 27
        versionName = "2.0.17"
        // liturgy 27 (2.0.17)
        // god's word 27 (2.0.17)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("godswordKeys") {
            storeFile = file("../gods_word_2021.jks")
            keyAlias = "StPaulsGodsWord2020"
            storePassword = "StPaulsGodsWord2020@@"
            keyPassword = "StPaulsGodsWord2020@@"
        }
        create("liturgyKeys") {
            storeFile = file("../ireland_gods_word_2021.jks")
            keyAlias = "ireland_gods_word_2020"
            storePassword = "ireland_gods_word_2020@#\$%"
            keyPassword = "ireland_gods_word_2020@#\$%"
        }
    }

    flavorDimensions += "default"
    productFlavors {
        create("godsword") {
            dimension = "default"
            applicationId = "com.stpauls.godsword"
            resValue("string", "app_name", "God\\'s Word")
            buildConfigField("String", "BASE_URL", "\"http://biblediary.in/admin/public/index.php/api/\"")
            manifestPlaceholders += mapOf(
                "sqrIcon" to "@mipmap/logo_square_godsword",
                "rndIcon" to "@mipmap/logo_round_godsword"
            )
            signingConfig = signingConfigs.getByName("godswordKeys")
        }
        create("liturgy") {
            dimension = "default"
            applicationId = "com.stpauls.dailyliturgy"
            resValue("string", "app_name", "Daily Liturgy")
            buildConfigField("String", "BASE_URL", "\"https://liturgyforeachday.com/api/\"")
            manifestPlaceholders += mapOf(
                "sqrIcon" to "@mipmap/logo_square",
                "rndIcon" to "@mipmap/logo_round"
            )
            signingConfig = signingConfigs.getByName("liturgyKeys")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isDebuggable = false
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
//    lint {
//        baseline = file("lint-baseline.xml")
//    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = true
    }
    packagingOptions {
        resources {
            excludes += setOf(
                "credentials.txt",
                "credentialsIreland.txt",
                "gods_word_2021.jks",
                "ireland_gods_word_2021.jks",
                // Generic/common files to exclude
                "README.md",
                "LICENSE",
                "LICENSE.txt",
                "CHANGELOG.md",
                ".gitignore",
                ".gitattributes",
                ".DS_Store",
                "Thumbs.db",
                "META-INF/AL2.0",
                "META-INF/LGPL2.1",
                "META-INF/LICENSE*",
                "META-INF/NOTICE*",
                "META-INF/DEPENDENCIES",
                "META-INF/INDEX.LIST"
            )
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.0"))

    implementation("androidx.core:core-ktx:1.3.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.0.1")
    testImplementation("junit:junit:4.13")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")

    // Material Design
    implementation("com.google.android.material:material:1.11.0")

    // Dynamic Dimension
    implementation("com.intuit.sdp:sdp-android:1.0.6")
    implementation("com.intuit.ssp:ssp-android:1.0.6")

    // Swipe to Refresh
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0") {
        exclude(group = "com.android.support")
    }
    kapt("com.github.bumptech.glide:compiler:4.16.0")

    // Room
    val room_version = "2.3.0"
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.8.1")

    // Firebase
    implementation("com.google.firebase:firebase-dynamic-links:20.1.1")
    implementation("com.google.firebase:firebase-analytics:19.0.2")
    implementation("com.google.firebase:firebase-crashlytics:18.2.3")
    implementation("com.google.firebase:firebase-messaging-ktx:22.0.0")

    // Media
    implementation("androidx.media:media:1.6.0")

    // Library to decode X-HTML into Java String
    implementation("org.apache.commons:commons-lang3:3.0")

    // Jetpack Compose BOM and Runtime
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
}

apply(plugin = "com.google.gms.google-services")
apply(plugin = "com.google.firebase.crashlytics")