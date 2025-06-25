package com.example.dwiDaoGenerator.checklist.generated;

import java.util.List;
import java.util.function.Function;

/**
 * Placeholder types for Spring Data compatibility
 * Auto-generated simplified versions to avoid Spring Data dependencies
 */
public class SpringDataTypes {

    /**
     * Simplified Page interface
     */
    public interface Page<T> extends Iterable<T> {
        int getTotalPages();
        long getTotalElements();
        boolean hasNext();
        boolean isFirst();
        boolean isLast();
        boolean hasContent();
        List<T> getContent();
        int getNumber();
        int getSize();
        int getNumberOfElements();
        boolean hasPrevious();
        Pageable getPageable();
        Pageable nextPageable();
        Pageable previousPageable();
        <U> Page<U> map(Function<? super T, ? extends U> converter);
    }

    /**
     * Simplified Pageable interface
     */
    public interface Pageable {
        int getPageNumber();
        int getPageSize();
        long getOffset();
        Sort getSort();
        Pageable next();
        Pageable previousOrFirst();
        Pageable first();
        boolean hasPrevious();
        boolean isPaged();
        boolean isUnpaged();
        
        static Pageable unpaged() {
            return new UnpagedPageable();
        }
        
        static Pageable ofSize(int pageSize) {
            return new SimplePageable(0, pageSize, Sort.unsorted());
        }
    }

    /**
     * Simplified Sort interface
     */
    public interface Sort extends Iterable<Sort.Order> {
        Sort and(Sort sort);
        Sort ascending();
        Sort descending();
        boolean isEmpty();
        boolean isSorted();
        boolean isUnsorted();
        
        static Sort by(String... properties) {
            return new SimpleSort(properties);
        }
        
        static Sort unsorted() {
            return new UnsortedSort();
        }
        
        /**
         * Sort order
         */
        public static class Order {
            private final Direction direction;
            private final String property;
            
            public Order(Direction direction, String property) {
                this.direction = direction;
                this.property = property;
            }
            
            public Direction getDirection() { return direction; }
            public String getProperty() { return property; }
            
            public static Order asc(String property) {
                return new Order(Direction.ASC, property);
            }
            
            public static Order desc(String property) {
                return new Order(Direction.DESC, property);
            }
        }
        
        /**
         * Sort direction
         */
        public enum Direction {
            ASC, DESC
        }
    }

    /**
     * Simplified Specification interface
     */
    public interface Specification<T> {
        // Placeholder for JPA Criteria API
        // In real implementation, this would work with CriteriaBuilder
    }

    // Implementation classes (simplified)

    private static class UnpagedPageable implements Pageable {
        @Override public int getPageNumber() { return 0; }
        @Override public int getPageSize() { return Integer.MAX_VALUE; }
        @Override public long getOffset() { return 0; }
        @Override public Sort getSort() { return Sort.unsorted(); }
        @Override public Pageable next() { return this; }
        @Override public Pageable previousOrFirst() { return this; }
        @Override public Pageable first() { return this; }
        @Override public boolean hasPrevious() { return false; }
        @Override public boolean isPaged() { return false; }
        @Override public boolean isUnpaged() { return true; }
    }

    private static class SimplePageable implements Pageable {
        private final int page; private final int size; private final Sort sort;
        public SimplePageable(int page, int size, Sort sort) { this.page = page; this.size = size; this.sort = sort; }
        @Override public int getPageNumber() { return page; }
        @Override public int getPageSize() { return size; }
        @Override public long getOffset() { return (long) page * size; }
        @Override public Sort getSort() { return sort; }
        @Override public Pageable next() { return new SimplePageable(page + 1, size, sort); }
        @Override public Pageable previousOrFirst() { return page == 0 ? this : new SimplePageable(page - 1, size, sort); }
        @Override public Pageable first() { return new SimplePageable(0, size, sort); }
        @Override public boolean hasPrevious() { return page > 0; }
        @Override public boolean isPaged() { return true; }
        @Override public boolean isUnpaged() { return false; }
    }

    private static class SimpleSort implements Sort {
        private final String[] properties;
        public SimpleSort(String... properties) { this.properties = properties; }
        @Override public Sort and(Sort sort) { return this; }
        @Override public Sort ascending() { return this; }
        @Override public Sort descending() { return this; }
        @Override public boolean isEmpty() { return properties.length == 0; }
        @Override public boolean isSorted() { return properties.length > 0; }
        @Override public boolean isUnsorted() { return properties.length == 0; }
        @Override public java.util.Iterator<Order> iterator() {
            return java.util.Arrays.stream(properties).map(prop -> Order.asc(prop)).iterator();
        }
    }

    private static class UnsortedSort implements Sort {
        @Override public Sort and(Sort sort) { return sort; }
        @Override public Sort ascending() { return this; }
        @Override public Sort descending() { return this; }
        @Override public boolean isEmpty() { return true; }
        @Override public boolean isSorted() { return false; }
        @Override public boolean isUnsorted() { return true; }
        @Override public java.util.Iterator<Order> iterator() {
            return java.util.Collections.emptyIterator();
        }
    }
}
