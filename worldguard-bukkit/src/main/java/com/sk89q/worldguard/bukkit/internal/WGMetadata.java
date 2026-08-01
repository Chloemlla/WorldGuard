/*
 * WorldGuard, a suite of tools for Minecraft
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) WorldGuard team and contributors
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.sk89q.worldguard.bukkit.internal;

import org.bukkit.entity.Entity;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Utility methods for dealing with metadata on entities.
 *
 * <p>Replaces the deprecated Bukkit Metadata API with a UUID-based map.</p>
 */
public final class WGMetadata {

    private static final Map<UUID, Map<String, Object>> METADATA_MAP = new HashMap<>();

    private WGMetadata() {
    }

    /**
     * Add some metadata to an entity.
     *
     * @param target the entity
     * @param key the key
     * @param value the value
     */
    public static void put(Entity target, String key, Object value) {
        METADATA_MAP.computeIfAbsent(target.getUniqueId(), k -> new HashMap<>()).put(key, value);
    }

    /**
     * Get metadata value from an entity.
     *
     * @param target the entity
     * @param key the key
     * @param expected the type of the value
     * @param <T> the type of the value
     * @return a value, or {@code null} if one does not exist
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T getIfPresent(Entity target, String key, Class<T> expected) {
        Map<String, Object> entityData = METADATA_MAP.get(target.getUniqueId());
        if (entityData == null) {
            return null;
        }
        Object value = entityData.get(key);
        if (expected.isInstance(value)) {
            return (T) value;
        }
        return null;
    }

    /**
     * Removes metadata from an entity.
     *
     * @param target the entity
     * @param key the key
     */
    public static void remove(Entity target, String key) {
        Map<String, Object> entityData = METADATA_MAP.get(target.getUniqueId());
        if (entityData != null) {
            entityData.remove(key);
            if (entityData.isEmpty()) {
                METADATA_MAP.remove(target.getUniqueId());
            }
        }
    }

    /**
     * Clean up metadata for an entity (e.g. on entity removal).
     *
     * @param target the entity
     */
    public static void removeAll(Entity target) {
        METADATA_MAP.remove(target.getUniqueId());
    }
}