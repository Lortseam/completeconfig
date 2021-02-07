# CompleteConfig
CompleteConfig is a flexible, all-in-one configuration API for [Fabric](https://fabricmc.net/) mods.
It aims to be full-featured and extensible.

## Highlights
Beside the basic elements of a config library, CompleteConfig offers some unique features:
* Multiple configs
* Nested class support - easily create a POJO structure
* Simple integration into existing code
* Listeners - observe your config entries
* User-friendly save format
* Commentable files - comments are retained permanently
* Configurable GUI generation
* Extension system

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

    // Adds CompleteConfig and bundles it within the mod's jar file
    // Replace Tag with the current version you can find above
    modImplementation ("com.gitlab.Lortseam:completeconfig:Tag")
    include("com.gitlab.Lortseam:completeconfig:Tag")
    
    // Bundles Cloth Config within the mod's jar file
    // Only required if you want to display a config GUI and don't provide your own screen builder
    // Replace Version with the current version
    include("me.shedaniel.cloth:config-2:Version")
}
```

## Usage
Usage instructions can be found in the [wiki](https://gitlab.com/Lortseam/completeconfig/-/wikis/home).

## License
This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.