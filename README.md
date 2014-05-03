Android Bluetooth Library
=====================

This library allows you to easily create a socket bluetooth connection for two android devices with one server and one client. This library is compatible with the Android SDK 2.3 to 4.4.

![ScreenShot](img/nexus_client.png)![ScreenShot](img/nexus_server.png)


For documentation and additional information see [the website][1].

Download
--------
Download __[the latest JAR][2]__  or grab via Maven:
```xml
  <dependencies>
    <dependency>
      <groupId>com.ramimartin.bluetooth</groupId>
      <artifactId>AndroidBluetoothLibrary</artifactId>
      <version>1.0.0-SNAPSHOT</version>
    </dependency>
  </dependencies>

  <repositories>
    <repository>
      <id>arissa34-ftp</id>
      <name>Arissa Ftp</name>
      <url>http://arissa34.free.fr/maven2</url>
    </repository>
  </repositories>
```
or Gradle:
```groovy
repositories {
    maven {
        url "http://arissa34.free.fr/maven2/com/ramimartin/bluetooth/AndroidBluetoothLibrary/"
        artifactUrls mavenLocal()
    }
    mavenCentral()
}

dependencies {
    compile 'com.ramimartin.bluetooth:AndroidBluetoothLibrary:1.0.0-SNAPSHOT'
}
    
```
License
-------

    Copyright 2014 Rami Martin

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    
[1]: http://arissa34.github.io/Android-Bluetooth-Library/
[2]: http://arissa34.free.fr/maven2/com/ramimartin/bluetooth/AndroidBluetoothLibrary/1.0.0-SNAPSHOT/AndroidBluetoothLibrary-1.0.0-SNAPSHOT.jar
