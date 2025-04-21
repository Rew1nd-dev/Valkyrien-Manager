package com.verr1.valkyrienmanager.manager.db.v1;

import com.verr1.valkyrienmanager.VManagerServer;
import com.verr1.valkyrienmanager.foundation.data.VOwnerData;
import com.verr1.valkyrienmanager.foundation.data.VTag;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

import static com.verr1.valkyrienmanager.util.ComponentUtil.*;

public class VDataExtension {
    public static final VDataExtension EMPTY = new VDataExtension(0, 0, new HashSet<>());


    private final long id;
    private final long createdTimeStamp;

    public VOwnerData getOwner() {
        return owner;
    }

    private VOwnerData owner = VOwnerData.NOT_OWNED;
    private final Set<VTag> tags = new HashSet<>();
    private final Set<VOwnerData> playersAroundOnCreation = new HashSet<>();

    private VDataExtension(long id, long createdTimeStamp, Set<VOwnerData> playersAroundOnCreation){
        this.id = id;
        this.createdTimeStamp = createdTimeStamp;
        this.playersAroundOnCreation.addAll(playersAroundOnCreation);
    }


    public long getId() {
        return id;
    }

    public long getCreatedTimeStamp() {
        return createdTimeStamp;
    }

    public Set<VOwnerData> getPlayersAroundOnCreation() {
        return playersAroundOnCreation;
    }

    public void setOwnBy(Player player){
        this.owner = VOwnerData.of(player);
    }

    public void addTag(VTag tag){
        tags.add(tag);
    }

    public void removeTag(VTag tag){
        tags.remove(tag);
    }

    public Set<VTag> getTags() {
        return tags;
    }

    public boolean hasTag(VTag tag){
        return tags.contains(tag);
    }


    public List<Component> toComponent(){
        return List.of(
                Component.literal("Ship ID: " + id).withStyle(style -> style.withBold(true).withColor(ChatFormatting.GOLD)),
                Component.literal("Created: " + VManagerServer.manager().timer.ago(createdTimeStamp) + " ago"),
                Component.literal("Players Around On Creation: " + playersAroundOnCreation)
        );
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof VDataExtension that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ShipDataExtension{" +
                "id=" + id +
                ", createdTimeStamp=" + createdTimeStamp +
                ", playersAroundOnCreation=" + playersAroundOnCreation +
                '}';
    }

    public static class builder{
        private long id = 0;
        private long createdTimeStamp = 0;
        private final Set<VOwnerData> playersAroundOnCreation = new HashSet<>();

        public VDataExtension build(){
            return new VDataExtension(id, createdTimeStamp, playersAroundOnCreation);
        }

        public builder withID(long id){
            this.id = id;
            return this;
        }

        public builder withCreatedTimeStamp(long createdTimeStamp){
            this.createdTimeStamp = createdTimeStamp;
            return this;
        }

        public builder withPlayersAroundOnCreation(Collection<ServerPlayer> playersAroundOnCreation){
            this.playersAroundOnCreation
                    .addAll(
                        playersAroundOnCreation
                            .stream()
                            .map(player -> new
                                    VOwnerData(
                                    player.getName().getString(),
                                    player.getUUID())
                            ).toList()
                    );
            return this;
        }

        public builder withPlayer(ServerPlayer player){
            this.playersAroundOnCreation.add(new VOwnerData(player.getName().getString(), player.getUUID()));
            return this;
        }
    }

    public enum Field{
        ID(
                "ID",
                vDataExtension -> titleWithContent("id", "" + vDataExtension.getId(), titleStyle(), contentStyle())
        ),
        CREATED_TIMESTAMP(
                "Created Timestamp",
                vDataExtension -> titleWithContent("Created", VManagerServer.manager().timer.ago(vDataExtension.getCreatedTimeStamp()), titleStyle(), contentStyle())
        ),
        PLAYERS_AROUND_ON_CREATION(
                "Created Around: ",
                vDataExtension -> titleWithContent("Players Around On Creation", "" + vDataExtension.getPlayersAroundOnCreation(), titleStyle(), contentStyle())
        ),

        OWNER(
                "Owner",
                vDataExtension -> titleWithContent("Owner", "" + vDataExtension.owner, titleStyle(), contentStyle())
        ),

        TAGS(
                "Tags",
                vDataExtension -> titleWithContent("Tags", "" + vDataExtension.getTags(), titleStyle(), contentStyle())
        );

        private final String name;

        private Function<VDataExtension, Component> getter;

        Field(String name, @NotNull Function<VDataExtension, Component> getter) {
            this.name = name;
            this.getter = getter;
        }

        public Component toComponent(VDataExtension vDataExtension){
            return getter.apply(vDataExtension);
        }

        public String getName() {
            return name;
        }
    }

}


