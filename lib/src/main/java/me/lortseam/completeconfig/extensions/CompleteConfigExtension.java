package me.lortseam.completeconfig.extensions;

public interface CompleteConfigExtension extends ConfigExtensionPattern {

    default ConfigExtensionPattern client() {
        return null;
    }

    default ConfigExtensionPattern server() {
        return null;
    }

}
