package io.ikws4.weiju.editor.lua;

import androidx.annotation.NonNull;

import io.github.rosemoe.sora.lang.EmptyLanguage;
import io.github.rosemoe.sora.lang.analysis.AnalyzeManager;
import io.github.rosemoe.sora.widget.SymbolPairMatch;
import io.ikws4.jsitter.TSLanguages;
import io.ikws4.weiju.editor.LuaScmsKt;
import io.ikws4.weiju.editor.TSAnalyzeManager;

public class LuaLanguage extends EmptyLanguage {
    private AnalyzeManager analyzeManager;

    @NonNull
    @Override
    public AnalyzeManager getAnalyzeManager() {
        if (analyzeManager == null) {
            analyzeManager = new TSAnalyzeManager(TSLanguages.lua(), LuaScmsKt.HIGHTLIGHT);
        }
        return analyzeManager;
    }

    @Override
    public SymbolPairMatch getSymbolPairs() {
        return new SymbolPairMatch.DefaultSymbolPairs();
    }
}
