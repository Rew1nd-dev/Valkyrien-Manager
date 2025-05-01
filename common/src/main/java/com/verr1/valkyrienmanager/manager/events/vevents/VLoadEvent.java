package com.verr1.valkyrienmanager.manager.events.vevents;

import java.util.Map;

public record VLoadEvent(Long id) implements ILuaConvertible{

    @Override
    public Object toLua() {
        return Map.of("id", id);
    }
}
