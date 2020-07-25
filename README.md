# CompleteConfig
CompleteConfig is a flexible, all-in-one configuration API for [Fabric](https://fabricmc.net/) mods.  
It takes care of creating and observing config entries, displaying them as GUI, and saving and loading the config.

## Goals
The main goal of this library is to provide a comprehensive configuration system solution.  
This means:
* Great flexibility in API usage
* Full framework, no other libraries are required
* Easy integration into existing code

## Setup
[![](https://jitpack.io/v/com.gitlab.Lortseam/completeconfig.svg)](https://jitpack.io/#com.gitlab.Lortseam/completeconfig)

To use the API, first add the JitPack repository to your `build.gradle`:
```groovy
repositories {
    ...
    maven { url 'https://jitpack.io' }
}
```
Then add CompleteConfig as dependency:
```groovy
dependencies {
    ...

    //This adds CompleteConfig and includes it in your mod's jar, so users don't have to install it
    //Replace Tag with the current version you can find above
    modApi 'com.gitlab.Lortseam:completeconfig:Tag'
    include 'com.gitlab.Lortseam:completeconfig:Tag'
    
    //This bundles Cloth Config in your mod's jar which is required for CompleteConfig
    //Replace Version with current version
    include 'me.shedaniel.cloth:config-2:Version'

    //Recommended: Adds Mod Menu to your game, so you can easily open and check your config GUI (see the Mod Menu documentation for more information)
    //Replace Version with current version
    modImplementation 'io.github.prospector:modmenu:Version'
}
```

## Usage
See the [wiki](https://gitlab.com/Lortseam/completeconfig/-/wikis/home) for usage instructions.  
You can also take a look at the [example mod](https://gitlab.com/Lortseam/completeconfig-example-mod) which uses many features of this API.

## License
This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.