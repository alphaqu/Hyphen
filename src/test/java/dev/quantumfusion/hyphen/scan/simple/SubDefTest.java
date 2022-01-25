package dev.quantumfusion.hyphen.scan.simple;

import dev.quantumfusion.hyphen.util.TestThis;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

@TestThis
public class SubDefTest {
    public Outer outer;

    public SubDefTest(Outer outer) {
        this.outer = outer;
    }

    public static class Outer implements List<Integer> {

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @NotNull
        @Override
        public Iterator<Integer> iterator() {
            return null;
        }

        @NotNull
        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @NotNull
        @Override
        public <T> T[] toArray(@NotNull T[] a) {
            return null;
        }

        @Override
        public boolean add(Integer integer) {
            return false;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(@NotNull Collection<?> c) {
            return false;
        }

        @Override
        public boolean addAll(@NotNull Collection<? extends Integer> c) {
            return false;
        }

        @Override
        public boolean addAll(int index, @NotNull Collection<? extends Integer> c) {
            return false;
        }

        @Override
        public boolean removeAll(@NotNull Collection<?> c) {
            return false;
        }

        @Override
        public boolean retainAll(@NotNull Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {

        }

        @Override
        public boolean equals(Object o) {
            return false;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public Integer get(int index) {
            return null;
        }

        @Override
        public Integer set(int index, Integer element) {
            return null;
        }

        @Override
        public void add(int index, Integer element) {

        }

        @Override
        public Integer remove(int index) {
            return null;
        }

        @Override
        public int indexOf(Object o) {
            return 0;
        }

        @Override
        public int lastIndexOf(Object o) {
            return 0;
        }

        @NotNull
        @Override
        public ListIterator<Integer> listIterator() {
            return null;
        }

        @NotNull
        @Override
        public ListIterator<Integer> listIterator(int index) {
            return null;
        }

        @NotNull
        @Override
        public List<Integer> subList(int fromIndex, int toIndex) {
            return null;
        }
    }


}
