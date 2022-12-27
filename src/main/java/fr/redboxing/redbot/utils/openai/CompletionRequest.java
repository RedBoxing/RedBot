package fr.redboxing.redbot.utils.openai;

public class CompletionRequest {
    private String model;
    private String prompt;
    private double temperature;
    private int max_tokens;
    private double top_p;
    private int frequency_penalty;
    private double presence_penalty;
    private String[] stop;
    private String user;
    private int logprobs;

    private CompletionRequest(String model, String prompt, double temperature, int max_tokens, double top_p, int frequency_penalty, double presence_penalty, String[] stop, String user, int logprobs) {
        this.model = model;
        this.prompt = prompt;
        this.temperature = temperature;
        this.max_tokens = max_tokens;
        this.top_p = top_p;
        this.frequency_penalty = frequency_penalty;
        this.presence_penalty = presence_penalty;
        this.stop = stop;
        this.user = user;
        this.logprobs = logprobs;
    }

    public static CompletionRequestBuilder builder() {
        return new CompletionRequestBuilder();
    }

    public static class CompletionRequestBuilder {
        private String model;
        private String prompt;
        private double temperature;
        private int max_tokens;
        private double top_p;
        private int frequency_penalty;
        private double presence_penalty;
        private String[] stop;
        private String user;
        private int logprobs;

        public CompletionRequestBuilder model(String model) {
            this.model = model;
            return this;
        }

        public CompletionRequestBuilder prompt(String prompt) {
            this.prompt = prompt;
            return this;
        }

        public CompletionRequestBuilder temperature(double temperature) {
            this.temperature = temperature;
            return this;
        }

        public CompletionRequestBuilder maxTokens(int max_tokens) {
            this.max_tokens = max_tokens;
            return this;
        }

        public CompletionRequestBuilder topP(double top_p) {
            this.top_p = top_p;
            return this;
        }

        public CompletionRequestBuilder frequencyPenalty(int frequency_penalty) {
            this.frequency_penalty = frequency_penalty;
            return this;
        }

        public CompletionRequestBuilder presencePenalty(double presence_penalty) {
            this.presence_penalty = presence_penalty;
            return this;
        }

        public CompletionRequestBuilder stop(String[] stop) {
            this.stop = stop;
            return this;
        }

        public CompletionRequestBuilder user(String user) {
            this.user = user;
            return this;
        }

        public CompletionRequestBuilder logprobs(int logprobs) {
            this.logprobs = logprobs;
            return this;
        }

        public CompletionRequest build() {
            return new CompletionRequest(model, prompt, temperature, max_tokens, top_p, frequency_penalty, presence_penalty, stop, user, logprobs);
        }
    }
}
