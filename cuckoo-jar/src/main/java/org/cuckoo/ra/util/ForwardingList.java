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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ForwardingList<E> implements List<E>, Serializable, Cloneable {

    private ArrayList<E> l = new ArrayList<E>();

    public int size() {
        return l.size();
    }

    public boolean isEmpty() {
        return l.isEmpty();
    }

    public boolean contains(Object o) {
        return l.contains(o);
    }

    public Iterator<E> iterator() {
        return l.iterator();
    }

    public Object[] toArray() {
        return l.toArray();
    }

    @SuppressWarnings({"SuspiciousToArrayCall"})
    public <T> T[] toArray(T[] a) {
        return l.toArray(a);
    }

    public boolean add(E e) {
        return l.add(e);
    }

    public boolean remove(Object o) {
        return l.remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        return l.containsAll(c);
    }

    public boolean addAll(Collection<? extends E> c) {
        return l.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends E> c) {
        return l.addAll(index, c);
    }

    public boolean removeAll(Collection<?> c) {
        return l.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return l.retainAll(c);
    }

    public void clear() {
        l.clear();
    }

    public E get(int index) {
        return l.get(index);
    }

    public E set(int index, E element) {
        return l.set(index, element);
    }

    public void add(int index, E element) {
        l.add(index, element);
    }

    public E remove(int index) {
        return l.remove(index);
    }

    public int indexOf(Object o) {
        return l.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return l.lastIndexOf(o);
    }

    public ListIterator<E> listIterator() {
        return l.listIterator();
    }

    public ListIterator<E> listIterator(int index) {
        return l.listIterator(index);
    }

    public List<E> subList(int fromIndex, int toIndex) {
        return l.subList(fromIndex, toIndex);
    }

    @Override
    public String toString() {
        return l.toString();
    }

    @Override
    @SuppressWarnings({"unchecked"})
    protected ForwardingList<E> clone() throws CloneNotSupportedException {
        ForwardingList<E> clone = (ForwardingList<E>) super.clone();
        clone.l = (ArrayList<E>) l.clone();
        return clone;
    }
}
