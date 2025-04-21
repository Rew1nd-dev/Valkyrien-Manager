package com.verr1.valkyrienmanager.manager.db.entry;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;


/**
 * This interface represents a dual-side database entry, providing methods for
 * server-side query and replacement, as well as client-side read operations.
 * It also includes methods for serialization and deserialization of data.
 *
 * @param <V> The type of the value stored in the database entry.
 */
public interface IEntry<V> {

    /**
     * Retrieves the value of the entry.
     *
     * @return The value of type V.
     */
    V get();

    /**
     * Retrieves the type of the value stored in the entry.
     *
     * @return The class type of the value.
     */
    Class<V> getType();

    /**
     * Retrieves the value of the entry for client-side read operations.
     * This method should only be called on the client side.
     *
     * @return The value of type V.
     */
    default V view() {
        return get();
    }

    /**
     * Sets the value of the entry. This method should only be called on the server side.
     * Some entries are view-only and do not support this operation.
     * Examples of such entries include ClusterEntry and SlugEntry.
     *
     * @param value The value to set, of type V.
     */
    void set(V value);

    /**
     * Serializes the content of the entry for saving on the server side.
     *
     * @return A {@link CompoundTag} containing the serialized data.
     */
    @NotNull
    default CompoundTag serialize() {
        return new CompoundTag();
    }

    /**
     * Deserializes the content of the entry using the provided tag.
     * This method is used to load data on the server side.
     *
     * @param tag The {@link CompoundTag} containing the data to load.
     */
    default void deserialize(@NotNull CompoundTag tag) {}

    /**
     * Serializes the content of the entry into a tag to be sent to the client.
     *
     * @return A {@link CompoundTag} containing the serialized data for the client.
     */
    @NotNull
    default CompoundTag serializePacket() {
        return serialize();
    }

    /**
     * Deserializes the content of the entry using the provided tag.
     * This method is used to update the content on the client side,
     * allowing {@link #view()} or {@link #get()} to query the updated data.
     *
     * @param tag The {@link CompoundTag} containing the data to update.
     */
    default void deserializePacket(@NotNull CompoundTag tag) {
        deserialize(tag);
    }
}

