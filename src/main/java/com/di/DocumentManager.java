package com.di;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
public class DocumentManager {

    private final Map<String, Document> documentStorage = new HashMap<>();
    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        if(document.getId()==null || document.getId().isEmpty()){
            document.setId(generateId());
            if (document.getCreated()==null){
                document.setCreated(Instant.now());
            }
        }else {
            Document storedDocument = documentStorage.get(document.getId());
            if (storedDocument!=null){
                document.setCreated(storedDocument.getCreated());
            }
        }
        documentStorage.put(document.getId(), document);
        return document;
    }

    private String generateId(){
        return UUID.randomUUID().toString();
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {

        return documentStorage.values().stream()
                .filter(document -> matchesTitlePrefixes(document, request.getTitlePrefixes()))
                .filter(document -> matchesContainsContents(document, request.containsContents))
                .filter(document -> matchesAuthorId(document,request.authorIds))
                .filter(document -> matchesCreatedFrom(document,request.createdFrom))
                .filter(document -> matchesCreatedTo(document,request.createdTo))
                .collect(Collectors.toList());
    }

    private boolean matchesTitlePrefixes(Document document, List<String> titlePrefixes){
        if (titlePrefixes==null || titlePrefixes.isEmpty()) return true;
        return titlePrefixes.stream().anyMatch(prefix -> document.getTitle().toLowerCase().startsWith(prefix.toLowerCase()));
    }

    private boolean matchesContainsContents(Document document, List<String> containsContents){
        if (containsContents==null || containsContents.isEmpty()) return true;
        return containsContents.stream().anyMatch(content-> document.getContent().toLowerCase().contains(content.toLowerCase()));
    }

    private boolean matchesAuthorId(Document document, List<String> authorIds){
        if(authorIds==null || authorIds.isEmpty()) return true;
        return authorIds.contains(document.getAuthor().getId());
    }

    private boolean matchesCreatedFrom(Document document, Instant createdFrom){
        if (createdFrom == null) return true;
        return !document.getCreated().isBefore(createdFrom);
    }

    private boolean matchesCreatedTo(Document document, Instant createdTo){
        if (createdTo == null) return true;
        return !document.getCreated().isAfter(createdTo);
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {

        return Optional.ofNullable(documentStorage.get(id));
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}