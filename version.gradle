/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/**
 * Shared file between builds so that they can all use the same dependency and
 * maven repositories.
 **/
ext.dependency = [:]
def versions = [:]
versions.android_gradle_plugin = '3.4.0'
versions.apache_commons = "2.5"
versions.arch_core = "2.0.1"
versions.atsl_core = "1.1.0"
versions.atsl_rules = "1.1.1"
versions.atsl_runner = "1.1.1"
versions.atsl_junit = "1.1.0"
versions.benchmark = "1.0.0-alpha02"
versions.constraint_layout = "2.0.0-alpha2"
versions.core_ktx = "1.0.0"
versions.dagger = "2.16"
versions.dexmaker = "2.2.0"
versions.espresso = "3.1.1"
versions.fragment = "1.1.0-alpha09"
versions.glide = "4.9.0"
versions.hamcrest = "1.3"
versions.junit = "4.12"
versions.kotlin = "1.3.40"
versions.lifecycle = "2.0.0"
versions.mockito = "2.25.0"
versions.mockito_all = "1.10.19"
versions.mockito_android = "2.25.0"
versions.mockwebserver = "3.8.1"
versions.navigation = "2.1.0-alpha04"
versions.okhttp_logging_interceptor = "3.9.0"
versions.paging = "2.1.0-rc01"
versions.retrofit = "2.6.2"
versions.robolectric = "4.2"
versions.room = "2.1.0-alpha04"
versions.rxandroid = "2.1.1"
versions.rxjava = "2.2.14"
versions.support = "1.0.0"
versions.timber = "4.5.1"
versions.webkit = "1.0.0"
versions.work = "2.1.0"

versions.fastjson = "1.1.71.android"
versions.gson = "2.8.6"
versions.okhttp = "3.14.0"
ext.versions = versions

def dependency = [:]
dependency.benchmark = "androidx.benchmark:benchmark:$versions.benchmark"
dependency.benchmark_gradle = "androidx.benchmark:benchmark-gradle-plugin:$versions.benchmark"

def support = [:]
support.annotation = "androidx.annotation:annotation:$versions.support"
support.appcompat = "androidx.appcompat:appcompat:$versions.support"
support.recyclerview = "androidx.recyclerview:recyclerview:$versions.support"
support.cardview = "androidx.cardview:cardview:$versions.support"
support.material = "com.google.android.material:material:$versions.support"
support.legacy_v4 = "androidx.legacy:legacy-support-v4:$versions.support"
support.legacy_core_utils = "androidx.legacy:legacy-support-core-utils:$versions.support"
support.core_ktx = "androidx.core:core-ktx:$versions.core_ktx"
support.fragment = "androidx.fragment:fragment:$versions.fragment"
support.fragment_ktx = "androidx.fragment:fragment-ktx:$versions.fragment"
support.fragment_testing = "androidx.fragment:fragment-testing:$versions.fragment"
dependency.support = support

def room = [:]
room.runtime = "androidx.room:room-runtime:$versions.room"
room.compiler = "androidx.room:room-compiler:$versions.room"
room.rxjava2 = "androidx.room:room-rxjava2:$versions.room"
room.testing = "androidx.room:room-testing:$versions.room"
dependency.room = room

def lifecycle = [:]
lifecycle.runtime = "androidx.lifecycle:lifecycle-runtime:$versions.lifecycle"
lifecycle.extensions = "androidx.lifecycle:lifecycle-extensions:$versions.lifecycle"
lifecycle.java8 = "androidx.lifecycle:lifecycle-common-java8:$versions.lifecycle"
lifecycle.compiler = "androidx.lifecycle:lifecycle-compiler:$versions.lifecycle"
lifecycle.viewmodel_ktx = "androidx.lifecycle:lifecycle-viewmodel-ktx:$versions.lifecycle"
dependency.lifecycle = lifecycle

def arch_core = [:]
arch_core.runtime = "androidx.arch.core:core-runtime:$versions.arch_core"
arch_core.testing = "androidx.arch.core:core-testing:$versions.arch_core"
dependency.arch_core = arch_core

def retrofit = [:]
retrofit.runtime = "com.squareup.retrofit2:retrofit:$versions.retrofit"
retrofit.gson = "com.squareup.retrofit2:converter-gson:$versions.retrofit"
retrofit.mock = "com.squareup.retrofit2:retrofit-mock:$versions.retrofit"
dependency.retrofit = retrofit
dependency.okhttp_logging_interceptor = "com.squareup.okhttp3:logging-interceptor:$versions.okhttp_logging_interceptor"

def dagger = [:]
dagger.runtime = "com.google.dagger:dagger:$versions.dagger"
dagger.android = "com.google.dagger:dagger-android:$versions.dagger"
dagger.android_support = "com.google.dagger:dagger-android-support:$versions.dagger"
dagger.compiler = "com.google.dagger:dagger-compiler:$versions.dagger"
dagger.android_support_compiler = "com.google.dagger:dagger-android-processor:$versions.dagger"

dependency.dagger = dagger

def espresso = [:]
espresso.core = "androidx.test.espresso:espresso-core:$versions.espresso"
espresso.contrib = "androidx.test.espresso:espresso-contrib:$versions.espresso"
espresso.intents = "androidx.test.espresso:espresso-intents:$versions.espresso"
dependency.espresso = espresso

def atsl = [:]
atsl.core = "androidx.test:core:$versions.atsl_core"
atsl.ext_junit = "androidx.test.ext:junit:$versions.atsl_junit"
atsl.runner = "androidx.test:runner:$versions.atsl_runner"
atsl.rules = "androidx.test:rules:$versions.atsl_rules"
dependency.atsl = atsl

def mockito = [:]
mockito.core = "org.mockito:mockito-core:$versions.mockito"
mockito.all = "org.mockito:mockito-all:$versions.mockito_all"
mockito.android = "org.mockito:mockito-android:$versions.mockito_android"
dependency.mockito = mockito

def kotlin = [:]
kotlin.stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$versions.kotlin"
kotlin.test = "org.jetbrains.kotlin:kotlin-test-junit:$versions.kotlin"
kotlin.plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$versions.kotlin"
kotlin.allopen = "org.jetbrains.kotlin:kotlin-allopen:$versions.kotlin"

dependency.kotlin = kotlin

dependency.paging_ktx = "androidx.paging:paging-runtime-ktx:$versions.paging"

def glide = [:]
glide.runtime = "com.github.bumptech.glide:glide:$versions.glide"
glide.compiler = "com.github.bumptech.glide:compiler:$versions.glide"
dependency.glide = glide

dependency.dexmaker = "com.linkedin.dexmaker:dexmaker-mockito:$versions.dexmaker"
dependency.constraint_layout = "androidx.constraintlayout:constraintlayout:$versions.constraint_layout"
dependency.timber = "com.jakewharton.timber:timber:$versions.timber"
dependency.junit = "junit:junit:$versions.junit"
dependency.mock_web_server = "com.squareup.okhttp3:mockwebserver:$versions.mockwebserver"
dependency.rxjava = "io.reactivex.rxjava2:rxjava:$versions.rxjava"
dependency.rxandroid = "io.reactivex.rxjava2:rxandroid:$versions.rxandroid"
dependency.hamcrest = "org.hamcrest:hamcrest-all:$versions.hamcrest"
dependency.android_gradle_plugin = "com.android.tools.build:gradle:$versions.android_gradle_plugin"
dependency.robolectric = "org.robolectric:robolectric:$versions.robolectric"
ext.dependency = dependency

def build_versions = [:]
build_versions.min_sdk = 22
build_versions.target_sdk = 29
build_versions.build_tools = "29.0.2"
ext.build_versions = build_versions

def webkit = [:]
webkit.runtime = "androidx.webkit:webkit:$versions.webkit"
dependency.webkit = webkit

def work = [:]
work.runtime = "androidx.work:work-runtime:$versions.work"
work.testing = "androidx.work:work-testing:$versions.work"
work.firebase = "androidx.work:work-firebase:$versions.work"
work.runtime_ktx = "androidx.work:work-runtime-ktx:$versions.work"
dependency.work = work

def navigation = [:]
navigation.runtime = "androidx.navigation:navigation-runtime:$versions.navigation"
navigation.runtime_ktx = "androidx.navigation:navigation-runtime-ktx:$versions.navigation"
navigation.fragment = "androidx.navigation:navigation-fragment:$versions.navigation"
navigation.fragment_ktx = "androidx.navigation:navigation-fragment-ktx:$versions.navigation"
navigation.ui = "androidx.navigation:navigation-ui:$versions.navigation"
navigation.ui_ktx = "androidx.navigation:navigation-ui-ktx:$versions.navigation"
navigation.safe_args_gradle_plugin = "androidx.navigation:navigation-safe-args-gradle-plugin:$versions.navigation"
dependency.navigation = navigation

def json = [:]
json.fastjson = "com.alibaba:fastjson:$versions.fastjson"
json.gson = "com.google.code.gson:gson:$versions.gson"
dependency.json = json

def okhttp = [:]
okhttp.runtime = "com.squareup.okhttp3:okhttp:$versions.okhttp"
dependency.okhttp = okhttp

ext.dependency = dependency
