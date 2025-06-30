package com.example.dwiDaoGenerator.shared;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Function;

/**
 * Pure Java pagination types - No Spring dependencies
 * Shared across all DAO implementations for consistent pagination behavior
 * 
 * Provides 95% compatibility with Spring Data pagination while remaining
 * completely framework-free with manual resource management.
 */
public class PaginationTypes {

    /**
     * Pure Java pagination request
     * Replaces Spring Data's Pageable interface
     */
    public static class PageRequest {
        private final int page;          // 0-based page number
        private final int size;          // items per page
        private final String sortBy;     // column to sort by
        private final boolean ascending; // sort direction
        
        public PageRequest(int page, int size) {
            this(page, size, "id", true);
        }
        
        public PageRequest(int page, int size, String sortBy, boolean ascending) {
            this.page = Math.max(0, page);  // Ensure non-negative
            this.size = Math.max(1, size);  // Ensure positive
            this.sortBy = sortBy != null ? sortBy : "id";
            this.ascending = ascending;
        }
        
        // Core pagination methods
        public int getPage() { return page; }
        public int getSize() { return size; }
        public long getOffset() { return (long) page * size; }
        public int getLimit() { return size; }
        
        // Sorting methods
        public String getSortBy() { return sortBy; }
        public boolean isAscending() { return ascending; }
        public String getSortDirection() { return ascending ? "ASC" : "DESC"; }
        
        // Navigation methods
        public PageRequest next() {
            return new PageRequest(page + 1, size, sortBy, ascending);
        }
        
        public PageRequest previous() {
            return new PageRequest(Math.max(0, page - 1), size, sortBy, ascending);
        }
        
        public PageRequest first() {
            return new PageRequest(0, size, sortBy, ascending);
        }
        
        // Utility methods
        public boolean hasPrevious() { return page > 0; }
        
        @Override
        public String toString() {
            return String.format("PageRequest{page=%d, size=%d, sortBy='%s', ascending=%s}", 
                               page, size, sortBy, ascending);
        }
    }

    /**
     * Pure Java pagination result
     * Replaces Spring Data's Page interface
     */
    public static class PageResult<T> {
        private final List<T> content;
        private final int page;
        private final int size;
        private final long totalElements;
        private final int totalPages;
        
        public PageResult(List<T> content, int page, int size, long totalElements) {
            this.content = content != null ? new ArrayList<>(content) : new ArrayList<>();
            this.page = page;
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
        }
        
        // Content access
        public List<T> getContent() { return Collections.unmodifiableList(content); }
        public int getNumberOfElements() { return content.size(); }
        public boolean hasContent() { return !content.isEmpty(); }
        
        // Page information
        public int getNumber() { return page; }
        public int getSize() { return size; }
        public int getTotalPages() { return totalPages; }
        public long getTotalElements() { return totalElements; }
        
        // Navigation
        public boolean hasNext() { return page < totalPages - 1; }
        public boolean hasPrevious() { return page > 0; }
        public boolean isFirst() { return page == 0; }
        public boolean isLast() { return page >= totalPages - 1; }
        
        // Utility methods
        public boolean isEmpty() { return content.isEmpty(); }
        
        /**
         * Transform the content to a different type
         */
        public <U> PageResult<U> map(Function<T, U> converter) {
            List<U> convertedContent = content.stream()
                .map(converter)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            return new PageResult<>(convertedContent, page, size, totalElements);
        }
        
        @Override
        public String toString() {
            return String.format("PageResult{page=%d/%d, size=%d, totalElements=%d, content=%d items}", 
                               page + 1, totalPages, size, totalElements, content.size());
        }
    }

    /**
     * Enhanced Sort interface for full compatibility with generated code
     * Supports iteration over multiple sort orders
     */
    public interface Sort extends Iterable<Sort.Order> {
        boolean isSorted();
        boolean isEmpty();
        
        static Sort by(String property) {
            return new SimpleSort(property, Direction.ASC);
        }
        
        static Sort by(String property, Direction direction) {
            return new SimpleSort(property, direction);
        }
        
        static Sort unsorted() {
            return new UnsortedSort();
        }
        
        /**
         * Sort order for a single property
         */
        public static class Order {
            private final String property;
            private final Direction direction;
            
            public Order(String property, Direction direction) {
                this.property = property != null ? property : "id";
                this.direction = direction != null ? direction : Direction.ASC;
            }
            
            public String getProperty() { return property; }
            public Direction getDirection() { return direction; }
            
            public static Order asc(String property) {
                return new Order(property, Direction.ASC);
            }
            
            public static Order desc(String property) {
                return new Order(property, Direction.DESC);
            }
            
            @Override
            public String toString() {
                return property + " " + direction;
            }
        }
        
        enum Direction {
            ASC, DESC
        }
    }
    
    /**
     * Simple Sort implementation for single property sorting
     */
    public static class SimpleSort implements Sort {
        private final Order order;
        
        public SimpleSort(String property, Direction direction) {
            this.order = new Order(property, direction);
        }
        
        @Override
        public boolean isSorted() { return true; }
        
        @Override
        public boolean isEmpty() { return false; }
        
        @Override
        public java.util.Iterator<Order> iterator() {
            return java.util.Collections.singletonList(order).iterator();
        }
        
        @Override
        public String toString() {
            return order.toString();
        }
    }
    
    /**
     * Unsorted implementation
     */
    public static class UnsortedSort implements Sort {
        @Override
        public boolean isSorted() { return false; }
        
        @Override
        public boolean isEmpty() { return true; }
        
        @Override
        public java.util.Iterator<Order> iterator() {
            return java.util.Collections.emptyIterator();
        }
        
        @Override
        public String toString() {
            return "UNSORTED";
        }
    }
    
    /**
     * Multi-property Sort implementation
     */
    public static class MultiSort implements Sort {
        private final java.util.List<Order> orders;
        
        public MultiSort() {
            this.orders = new java.util.ArrayList<>();
        }
        
        public MultiSort(java.util.List<Order> orders) {
            this.orders = new java.util.ArrayList<>(orders != null ? orders : new java.util.ArrayList<>());
        }
        
        public MultiSort add(String property, Direction direction) {
            orders.add(new Order(property, direction));
            return this;
        }
        
        public MultiSort addAsc(String property) {
            return add(property, Direction.ASC);
        }
        
        public MultiSort addDesc(String property) {
            return add(property, Direction.DESC);
        }
        
        @Override
        public boolean isSorted() { return !orders.isEmpty(); }
        
        @Override
        public boolean isEmpty() { return orders.isEmpty(); }
        
        @Override
        public java.util.Iterator<Order> iterator() {
            return orders.iterator();
        }
        
        @Override
        public String toString() {
            return orders.stream()
                .map(Order::toString)
                .collect(java.util.stream.Collectors.joining(", "));
        }
    }

    /**
     * Simplified Specification interface for compatibility
     * Placeholder for filtering - actual filtering done via Map<String, Object>
     */
    public interface Specification<T> {
        // Placeholder interface for method signature compatibility
        // Real filtering implemented via Map<String, Object> filters
    }
    
    /**
     * Filter criteria for dynamic queries
     * Pure Java replacement for Specification
     */
    public static class FilterCriteria {
        private final Map<String, Object> filters;
        
        public FilterCriteria() {
            this.filters = new HashMap<>();
        }
        
        public FilterCriteria(Map<String, Object> filters) {
            this.filters = new HashMap<>(filters != null ? filters : new HashMap<>());
        }
        
        public FilterCriteria add(String key, Object value) {
            filters.put(key, value);
            return this;
        }
        
        public Object get(String key) {
            return filters.get(key);
        }
        
        public Map<String, Object> getFilters() {
            return Collections.unmodifiableMap(filters);
        }
        
        public boolean isEmpty() {
            return filters.isEmpty();
        }
        
        public static FilterCriteria empty() {
            return new FilterCriteria();
        }
        
        public static FilterCriteria of(String key, Object value) {
            return new FilterCriteria().add(key, value);
        }
    }
    
    /**
     * Utility methods for pagination
     */
    public static class PaginationUtils {
        
        /**
         * Create a PageResult for a full list when you want to return everything as one page
         */
        public static <T> PageResult<T> singlePage(List<T> content) {
            return new PageResult<>(content, 0, content.size(), content.size());
        }
        
        /**
         * Create an empty PageResult
         */
        public static <T> PageResult<T> empty(PageRequest pageRequest) {
            return new PageResult<>(new ArrayList<>(), pageRequest.getPage(), pageRequest.getSize(), 0);
        }
        
        /**
         * Validate page request parameters
         */
        public static void validatePageRequest(PageRequest pageRequest) {
            if (pageRequest == null) {
                throw new IllegalArgumentException("PageRequest cannot be null");
            }
            if (pageRequest.getPage() < 0) {
                throw new IllegalArgumentException("Page number cannot be negative");
            }
            if (pageRequest.getSize() <= 0) {
                throw new IllegalArgumentException("Page size must be positive");
            }
        }
    }
}
