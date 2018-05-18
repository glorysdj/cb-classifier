package cn.azure.chatbot.classifier.models;

public class KnowledgeResult {
    private String id = "";
    private String collectionId = "";
    private String title = "";
    private String titleToSearch = "";
    private String description = "";
    private String descriptionToSearch = "";
    private String content = "";
    private String contentToSearch = "";
    private String service = "";
    private String url = "";
    private double score = 0.0;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleToSearch() {
        return titleToSearch;
    }

    public void setTitleToSearch(String titleToSearch) {
        this.titleToSearch = titleToSearch;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionToSearch() {
        return descriptionToSearch;
    }

    public void setDescriptionToSearch(String descriptionToSearch) {
        this.descriptionToSearch = descriptionToSearch;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentToSearch() {
        return contentToSearch;
    }

    public void setContentToSearch(String contentToSearch) {
        this.contentToSearch = contentToSearch;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
