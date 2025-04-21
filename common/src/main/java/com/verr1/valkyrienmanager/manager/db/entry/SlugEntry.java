package com.verr1.valkyrienmanager.manager.db.entry;

import com.verr1.valkyrienmanager.VManagerServer;
import com.verr1.valkyrienmanager.util.CompoundTagBuilder;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.api.ships.ServerShip;

public class SlugEntry implements IEntry<String>{

    private final long id;

    private String view = ""; // valid only in client side, server side view means nothing

    public SlugEntry(long id){
        this.id = id;
    }


    @Override
    public String get() {
        return VManagerServer.manager().shipOf(id).map(ServerShip::getSlug).orElse("Ship Not Found");
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public String view() {
        return view;
    }

    @Override
    public void set(String value) {
       VManagerServer.manager().shipOf(id).ifPresent(s -> s.setSlug(value));
    }

    @Override
    public @NotNull CompoundTag serializePacket() {
        return new CompoundTagBuilder().withString("slug", get()).build();
    }

    @Override
    public void deserializePacket(@NotNull CompoundTag tag) {
        view = tag.getString("slug");
    }
}
