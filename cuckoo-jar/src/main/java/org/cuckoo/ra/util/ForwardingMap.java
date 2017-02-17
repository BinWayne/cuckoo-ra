/*
 * Copyright (C) 2012-2017 akquinet tech@spree GmbH
 *
 * This file is part of the Cuckoo Resource Adapter for SAP.
 *
 * Cuckoo Resource Adapter for SAP is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Cuckoo Resource Adapter for SAP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with Cuckoo Resource Adapter for SAP. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cuckoo.ra.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ForwardingMap<K, V> implements Map<K, V>, Serializable, Cloneable {

    private HashMap<K, V> m = new HashMap<K, V>();

    public int size() {
        return m.size();
    }

    public boolean isEmpty() {
        return m.isEmpty();
    }

    public boolean containsKey(Object key) {
        return m.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return m.containsValue(value);
    }

    public V get(Object key) {
        return m.get(key);
    }

    public V put(K key, V value) {
        return m.put(key, value);
    }

    public V remove(Object key) {
        return m.remove(key);
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        this.m.putAll(m);
    }

    public void clear() {
        m.clear();
    }

    public Set<K> keySet() {
        return m.keySet();
    }

    public Collection<V> values() {
        return m.values();
    }

    public Set<Entry<K, V>> entrySet() {
        return m.entrySet();
    }

    @Override
    public String toString() {
        return m.toString();
    }

    @Override
    @SuppressWarnings({"unchecked"})
    protected ForwardingMap<K, V> clone() throws CloneNotSupportedException {
        ForwardingMap<K, V> clone = (ForwardingMap<K, V>) super.clone();
        clone.m = (HashMap<K, V>) m.clone();
        return clone;
    }
}
