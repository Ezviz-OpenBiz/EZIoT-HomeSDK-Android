# **快速集成**


本文主要介绍如何快速将app sdk接入到当前工程中去。介绍如何初始化，以及如何在demo中快速运行，快速上手萤石智能的移动应用开发。

## 集成Ezviz Iot Home SDK

前提条件：请确保电脑中已安装Android Studio，以及对应依赖环境，并可以运行安卓工程



## 第一步：创建Android工程

在Android Studio中新建工程



## 第二步：配置build.gradle文件

在工程级的build.gradle，添加如下配置。

```java
dependencies {
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.72"
        classpath "org.jetbrains.kotlin:kotlin-android-extensions:1.3.72"
        classpath "io.realm:realm-gradle-plugin:7.0.0"
}
```

在app级别的build.gradle中，添加如下配置

```java
plugins {
    id 'com.android.application'
    id 'kotlin-android'
    id 'kotlin-android-extensions'
    id 'kotlin-kapt'
    id 'realm-android'
}

//添加仓库
repositories {
    mavenCentral()
}

dependencies {
    api "org.jetbrains.kotlin:kotlin-stdlib:1.3.72"
    api 'androidx.core:core-ktx:1.3.2'

    api 'com.squareup.okhttp3:okhttp:3.14.9'
    api 'com.squareup.retrofit2:retrofit:2.4.0'
    api 'com.squareup.retrofit2:converter-gson:2.4.0'

    api "org.parceler:parceler-api:1.1.12"
    kapt "org.parceler:parceler:1.1.12"

    api 'androidx.annotation:annotation:1.3.0'
    kapt 'androidx.annotation:annotation:1.3.0'
    
    // 萤石IOT依赖
    implementation 'io.github.ezviz-open:eziot-sdk:1.0.0'
}
	
```

说明：库中使用了大量kotlin的语言，因此需要kotin语言环境支持。另外还是用了kapt的注解方式，因此需要加入kotlin-kapt插件。接入的时候，请优先使用文档中的三方库版本，防止出现因为版本不一致，导致运行异常的问题。

## 第三步：配置混淆

```
-keep class com.eziot.** {*;}
-dontwarn com.eziot.**

-dontwarn androidx.**
-keep class androidx.**{*;}

-keep class org.parceler.** {*;}
-dontwarn org.parceler.**

-dontwarn retrofit2.**
-keep class retrofit2.** {*;}

-dontwarn okhttp3.**
-keep interface okhttp3.** { *; }
-keep class okhttp3.** {*;}
-dontwarn okio.**
-keep class okio.** {*;}
```

## 第四步：在Application中初始化sdk

```
public class EZIoTApp extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
                EZIoTSDK.init(this,"appid")
	}
}
```

说明：关于appid可以去萤石官网中获取

## 第五步 ：运行Demo

demo演示了萤石app sdk的接入流程，因此在运行demo之前，请先完成一遍接入步骤



### demo介绍

#### 功能模块介绍

  * 登录模块：包含注册，登录，忘记密码功能
  * 家庭模块：包含添加家庭，删除家庭，修改家庭信息，邀请家庭成员和移除家庭成员等功能
  * 房间模块：包含添加房间，删除房间，移动房间设备，修改房间信息
  * 设备模块：包含从服务器中拉取设备列表，从本地数据库中拉取设备信息，对设备功能点进行操作，以及设备升级相关逻辑
  * 消息模块：包含消息列表展示，消息操作和消息免打扰开关逻辑
  * 配网模块：包含接触式配网




使用AndroidStudio打开工程IotSdkDemo编译运行即可

#### 主界面

<img src="https://resource.eziot.com/group1/M00/00/80/CtwQE2GUrHGACi31AADA2nZ-90k543.jpg" width = "300px" height = "700px"  />

