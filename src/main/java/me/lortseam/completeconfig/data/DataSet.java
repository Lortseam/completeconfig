package me.lortseam.completeconfig.data;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.lortseam.completeconfig.data.structure.DataPart;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;

import java.util.LinkedHashSet;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class DataSet<T extends DataPart> extends LinkedHashSet<T> {

    protected final TranslationIdentifier translation;

}
