package com.example.devpilotai.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GeminiModels {

    public static class Request {
        @SerializedName("contents")
        public List<Content> contents;
        @SerializedName("safetySettings")
        public List<SafetySetting> safetySettings;
        @SerializedName("generationConfig")
        public GenerationConfig generationConfig;

        public Request(List<Content> contents) {
            this.contents = contents;
        }

        public Request(List<Content> contents, List<SafetySetting> safetySettings, GenerationConfig generationConfig) {
            this.contents = contents;
            this.safetySettings = safetySettings;
            this.generationConfig = generationConfig;
        }
    }

    public static class Content {
        @SerializedName("role")
        public String role;
        @SerializedName("parts")
        public List<Part> parts;

        public Content(String role, List<Part> parts) {
            this.role = role;
            this.parts = parts;
        }
    }

    public static class Part {
        @SerializedName("text")
        public String text;

        public Part(String text) {
            this.text = text;
        }
    }

    public static class SafetySetting {
        @SerializedName("category")
        public String category;
        @SerializedName("threshold")
        public String threshold;

        public SafetySetting(String category, String threshold) {
            this.category = category;
            this.threshold = threshold;
        }
    }

    public static class GenerationConfig {
        @SerializedName("temperature")
        public Double temperature;
        @SerializedName("topK")
        public Integer topK;
        @SerializedName("topP")
        public Double topP;
        @SerializedName("maxOutputTokens")
        public Integer maxOutputTokens;
        @SerializedName("stopSequences")
        public List<String> stopSequences;

        public GenerationConfig(Double temperature, Integer maxOutputTokens) {
            this.temperature = temperature;
            this.maxOutputTokens = maxOutputTokens;
        }
    }

    public static class Response {
        @SerializedName("candidates")
        public List<Candidate> candidates;
        @SerializedName("promptFeedback")
        public PromptFeedback promptFeedback;
        @SerializedName("usageMetadata")
        public UsageMetadata usageMetadata;
    }

    public static class Candidate {
        @SerializedName("content")
        public Content content;
        @SerializedName("finishReason")
        public String finishReason;
        @SerializedName("index")
        public Integer index;
        @SerializedName("safetyRatings")
        public List<SafetyRating> safetyRatings;
    }

    public static class SafetyRating {
        @SerializedName("category")
        public String category;
        @SerializedName("probability")
        public String probability;
    }

    public static class PromptFeedback {
        @SerializedName("safetyRatings")
        public List<SafetyRating> safetyRatings;
    }

    public static class UsageMetadata {
        @SerializedName("promptTokenCount")
        public int promptTokenCount;
        @SerializedName("candidatesTokenCount")
        public int candidatesTokenCount;
        @SerializedName("totalTokenCount")
        public int totalTokenCount;
    }
}
