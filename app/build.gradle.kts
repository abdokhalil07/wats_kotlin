plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.ksp) // لتشغيل معالجات التعليقات التوضيحية (Annotation Processors)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.example.wtascopilot"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.wtas4"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
        freeCompilerArgs += "-language-version"
        freeCompilerArgs += "1.9"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // 1. Room Components
    // ------------------------------------
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.navigation.compose) // لدعم Coroutines و Kotlin

    // المعالج (Processor) للمُصنِّف Room
    ksp(libs.androidx.room.compiler)

    // الاختبارات
    testImplementation(libs.androidx.room.testing)

    // ------------------------------------
    // 2. Datastore Performance
    // ------------------------------------
    implementation(libs.androidx.datastore.preferences)

    // ------------------------------------
    // 3. Dagger - Hilt
    // ------------------------------------
    // المكتبة الأساسية
    implementation(libs.google.hilt.android)

    // المعالج (Processor) للمُصنِّف Hilt
    ksp(libs.google.hilt.compiler)

    // لاختبارات Hilt (اختياري)
    testImplementation(libs.google.hilt.testing)
    kspTest(libs.google.hilt.compiler) // معالج المُصنِّف لاختبارات Hilt

    // Retrofit Core: لإجراء طلبات API
    implementation(libs.squareup.retrofit)

    // Retrofit Converter: لتحويل JSON إلى كائنات Kotlin/Java (استخدام Gson كمثال)
    implementation(libs.squareup.retrofit.converter.gson)

    // OkHttp Logging Interceptor: لعرض تفاصيل طلبات الشبكة في Logcat (مفيدة أثناء التطوير)
    implementation(libs.squareup.okhttp.logging.interceptor)



    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}