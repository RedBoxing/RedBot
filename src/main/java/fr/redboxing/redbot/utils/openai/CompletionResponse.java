package fr.redboxing.redbot.utils.openai;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class CompletionResponse {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<CompletionChoice> choices;

    @Getter
    public static class CompletionChoice {
        private String text;
        private int index;
        private CompletionResponseChoiceLogprobs logprobs;
        private String finish_reason;

        @Override
        public String toString() {
            return "CompletionChoice{" +
                    "text='" + text + '\'' +
                    ", index=" + index +
                    ", logprobs=" + logprobs +
                    ", finish_reason='" + finish_reason + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "CompletionResponse{" +
                "id='" + id + '\'' +
                ", object='" + object + '\'' +
                ", created=" + created +
                ", model='" + model + '\'' +
                ", choices=" + choices +
                '}';
    }

    @Getter
    public static class CompletionResponseChoiceLogprobs {
        private List<String> tokens;
        private List<Integer> token_logprobs;
        private List<Map<String, Integer>> top_logprobs;
        private List<Integer> text_offset;

        @Override
        public String toString() {
            return "CompletionResponseChoiceLogprobs{" +
                    "tokens=" + tokens +
                    ", token_logprobs=" + token_logprobs +
                    ", top_logprobs=" + top_logprobs +
                    ", text_offset=" + text_offset +
                    '}';
        }
    }
}
