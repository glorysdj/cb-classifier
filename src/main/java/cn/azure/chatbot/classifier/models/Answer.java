package cn.azure.chatbot.classifier.models;

import java.util.ArrayList;
import java.util.List;

public class Answer {
    private List<FaqResult> faqResults = new ArrayList<>();
    private List<KnowledgeResult> knowledgeResults = new ArrayList<>();

    public List<FaqResult> getFaqResults() {
        return faqResults;
    }

    public void setFaqResults(List<FaqResult> faqResults) {
        this.faqResults = faqResults;
    }

    public List<KnowledgeResult> getKnowledgeResults() {
        return knowledgeResults;
    }

    public void setKnowledgeResults(List<KnowledgeResult> knowledgeResults) {
        this.knowledgeResults = knowledgeResults;
    }
}
