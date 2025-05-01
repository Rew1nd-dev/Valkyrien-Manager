package com.verr1.valkyrienmanager.manager.events.vevents;

import com.verr1.valkyrienmanager.manager.db.general.item.VItem;
import com.verr1.valkyrienmanager.util.Converter;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public record VRemoveEvent(Long id, @Nullable VItem data) implements ILuaConvertible{

    @Override
    public Object toLua() {
        return Map.of(
                "id", id,
                "data", Optional
                        .ofNullable(data)
                        .map(Converter::toLua)
                        .orElse(Map.of())
        );
    }

}
