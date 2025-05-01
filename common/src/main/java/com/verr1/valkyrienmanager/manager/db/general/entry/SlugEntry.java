package com.verr1.valkyrienmanager.manager.db.general.entry;

import com.verr1.valkyrienmanager.VManagerServer;
import com.verr1.valkyrienmanager.util.CompoundTagBuilder;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.api.ships.ServerShip;

public class SlugEntry extends IDOperatorEntry<String> {



    private String view = ""; // valid only in client side, server side view means nothing

    public SlugEntry(long id){
        super(id);
    }

    @Override
    String data(long id) {
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

    @Override
    public boolean shouldSave() {
        return false;
    }
}
