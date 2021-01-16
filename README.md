# CompleteConfig
CompleteConfig is a flexible, all-in-one configuration API for [Fabric](https://fabricmc.net/) mods.  
Because of its object-oriented and annotation-based system, it is very easy to integrate the library into your mod.

## Features
* Creating, grouping and observing config entries
* Building a customizable GUI
* File saving and loading

## Setup
[![](https://jitpack.io/v/com.gitlab.Lortseam/completeconfig.svg)](https://jitpack.io/#com.gitlab.Lortseam/completeconfig)

To use the library, first add the JitPack repository to your `build.gradle`:
```groovy
repositories {
    [...]
    maven { url 'https://jitpack.io' }
}
```
Then add CompleteConfig as dependency:
```groovy
dependencies {
    [...]

    // The following lines add CompleteConfig and include the required dependencies in your mod's jar, so users don't have to download them
    // Replace Tag with the current version you can find above
    modImplementation ("com.gitlab.Lortseam:completeconfig:Tag")
    include("com.gitlab.Lortseam:completeconfig:Tag")
    // Replace Version with the current version
    include("org.spongepowered:configurate-hocon:Version")
    
    // This bundles Cloth Config in your mod's jar
    // Only required if you want to display a GUI based on your mod's config and don't provide your own GUI generation
    // Replace Version with the current version
    include("me.shedaniel.cloth:config-2:Version")
}
```

## Usage
See the [wiki](https://gitlab.com/Lortseam/completeconfig/-/wikis/home) for usage instructions.  

## License
This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.