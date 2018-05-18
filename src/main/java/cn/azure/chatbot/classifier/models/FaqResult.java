package cn.azure.chatbot.classifier.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class FaqResult {
    private String category = "";
    @SerializedName("category-search")
    private String categoryToSearch = "";
    private String question = "";
    @SerializedName("question-search")
    private String questionToSearch = "";
    private String answer = "";
    @SerializedName("answer-search")
    private String answerToSearch = "";
    private String source = "";
    private String eTag = "";
    @SerializedName("timestamp")
    private Date timestamp = new Date();
    private String id = "";
    private double score = 0.0;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryToSearch() {
        return categoryToSearch;
    }

    public void setCategoryToSearch(String categoryToSearch) {
        this.categoryToSearch = categoryToSearch;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestionToSearch() {
        return questionToSearch;
    }

    public void setQuestionToSearch(String questionToSearch) {
        this.questionToSearch = questionToSearch;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswerToSearch() {
        return answerToSearch;
    }

    public void setAnswerToSearch(String answerToSearch) {
        this.answerToSearch = answerToSearch;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String geteTag() {
        return eTag;
    }

    public void seteTag(String eTag) {
        this.eTag = eTag;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
