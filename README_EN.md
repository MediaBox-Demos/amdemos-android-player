# amdemos-android-player

A demo project for Apsara Video Player SDK.

## **Demo Compilation**

### **License**

If you do not configure your own license, you will not be able to run the project.

[Manage licenses](https://www.alibabacloud.com/help/en/vod/developer-reference/license-authorization-and-management)

[Bind a license](https://www.alibabacloud.com/help/en/vod/developer-reference/access-to-license)

### **Environment**

* Gradle 7.5-bin, Plugin Version 7.1.2

* **JDK 11**

> Setting up JDK 11: Preferences -> Build, Execution, Deployment -> Build Tools -> Gradle -> Gradle JDK -> Choose 11 (if 11 is not available, please upgrade your Android Studio version)

### **IDE**

* Android Studio

### **Compilation Methods**

The current player demo project has undergone structural improvements to support both all-in-one and standalone compilation, enhancing development efficiency.

- **Standalone Compilation (Recommended)**

1. ou need to copy the basic module AndroidThirdParty from the AlivcAIODemo project to the current directory.
2. Open and run the project with Android Studio using the current directory as the target folder to achieve standalone compilation of the player module.

- **All-in-one Compilation**

1. The current project is embedded within the AlivcAIODemo project.
2. You can directly open and run AlivcAIODemo with Android Studio to achieve multi-module compilation.

### **Compilation Configuration**

1. If you are using**All-in-one Compilation**, the underlying SDK utilizes the Audio-Video Terminal SDK (AliVCSDK).
2. If you are using**Standalone Compilation**, you can configure the underlying SDK to use either the player SDK (AliyunPlayer) or the Audio-Video Terminal SDK.
   1. The `allInOne` compilation configuration in the `gradle.properties` file, determines the SDK type used.
   2. true, uses the Audio-Video Terminal SDK; false, uses the player SDK.

## **SDK Integration**

* **Version Update Records**

[SDK下载](https://help.aliyun.com/zh/vod/developer-reference/sdk-download)

[SDK download](https://www.alibabacloud.com/help/en/vod/developer-reference/sdk-download)

* **Quick Integration**

[快速集成](https://help.aliyun.com/zh/vod/developer-reference/quick-integration-1)

[Quick integration](https://www.alibabacloud.com/help/en/vod/developer-reference/quick-integration-1)

**Note: If you integrate both the live streaming push SDK and the player SDK, there will be conflict issues. You can use the [音视频终端 SDK](https://help.aliyun.com/document_detail/2391304.html)to avoid conflicts.**

## **Reference**

### **Product Official Website**

* [播放器SDK](https://help.aliyun.com/zh/vod/developer-reference/apsaravideo-player-sdk/)
* [ApsaraVideo Player SDK](https://www.alibabacloud.com/help/en/vod/developer-reference/apsaravideo-player-sdk/)
* [阿里云·视频点播](https://www.aliyun.com/product/vod)
* [ApsaraVideo VOD](https://www.alibabacloud.com/en/product/apsaravideo-for-vod)

### **API Documentation**

* [接口说明](https://help.aliyun.com/zh/vod/developer-reference/api-reference-android-player)
* [API reference](https://www.alibabacloud.com/help/en/vod/developer-reference/api-reference-android-player)

### **Console**

* [视频点播控制台](https://vod.console.aliyun.com)
* [License控制台](https://live.console.aliyun.com/connect_microphone/demo#/sdks/license)

## **Help**

If you have any questions or suggestions regarding the player SDK, feel free to join the Alibaba Cloud Player SDK developer group on DingTalk by searching for group number 31882553.

[FAQ about ApsaraVideo Player](https://www.alibabacloud.com/help/en/vod/support/faq-about-apsaravideo-player/)

[ApsaraVideo Player single-point tracing](https://www.alibabacloud.com/help/en/vod/user-guide/single-point-tracing)

