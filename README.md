# CompleteConfig
CompleteConfig is a flexible, all-in-one configuration API for [Fabric](https://fabricmc.net/) mods.

## Highlights
Beside the basic elements of a config library, CompleteConfig offers the following unique features:
* Simple integration into existing code
* Support for multiple configs
* Nested class resolution — easily create a grouped structure
* Listeners — observe your config entries
* User-friendly save format
* Commentable files — comments are retained permanently
* Customizable GUI generation
* Fully extensible via extension system

## Setup
[![](https://jitpack.io/v/com.gitlab.Lortseam/completeconfig.svg)](https://jitpack.io/#com.gitlab.Lortseam/completeconfig)

To use the library, first add the JitPack repository to your `build.gradle` file:
```groovy
repositories {
    [...]
    maven { url 'https://jitpack.io' }
}
```
Then add CompleteConfig to the dependencies:
```groovy
dependencies {
    [...]
    
    // Replace Tag with the current version you can find above
    modImplementation("com.gitlab.Lortseam:completeconfig:Tag")
    // Optional: Bundles the library within the mod's jar file, so users don't have to download it seperately
    include("com.gitlab.Lortseam:completeconfig:Tag")
}
```

## Usage
Usage instructions can be found in the [wiki](https://gitlab.com/Lortseam/completeconfig/-/wikis/home).

## License
This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.