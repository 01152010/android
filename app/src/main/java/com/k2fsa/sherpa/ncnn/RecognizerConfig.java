package com.k2fsa.sherpa.ncnn;


public class RecognizerConfig {
    private com.k2fsa.sherpa.ncnn.FeatureExtractorConfig featConfig;
    private com.k2fsa.sherpa.ncnn.ModelConfig modelConfig;
    private com.k2fsa.sherpa.ncnn.DecoderConfig decoderConfig;
    private boolean enableEndpoint = true;
    private float rule1MinTrailingSilence = 2.4f;
    private float rule2MinTrailingSilence = 1.0f;
    private float rule3MinUtteranceLength = 30.0f;
    private String hotwordsFile = "";
    private float hotwordsScore = 1.5f;

    public RecognizerConfig(com.k2fsa.sherpa.ncnn.FeatureExtractorConfig featConfig, com.k2fsa.sherpa.ncnn.ModelConfig modelConfig,
                            com.k2fsa.sherpa.ncnn.DecoderConfig decoderConfig) {
        this.featConfig = featConfig;
        this.modelConfig = modelConfig;
        this.decoderConfig = decoderConfig;
    }

    // Getters and setters
    public com.k2fsa.sherpa.ncnn.FeatureExtractorConfig getFeatConfig() { return featConfig; }
    public void setFeatConfig(com.k2fsa.sherpa.ncnn.FeatureExtractorConfig featConfig) { this.featConfig = featConfig; }

    public com.k2fsa.sherpa.ncnn.ModelConfig getModelConfig() { return modelConfig; }
    public void setModelConfig(com.k2fsa.sherpa.ncnn.ModelConfig modelConfig) { this.modelConfig = modelConfig; }

    public com.k2fsa.sherpa.ncnn.DecoderConfig getDecoderConfig() { return decoderConfig; }
    public void setDecoderConfig(com.k2fsa.sherpa.ncnn.DecoderConfig decoderConfig) { this.decoderConfig = decoderConfig; }

    public boolean isEnableEndpoint() { return enableEndpoint; }
    public void setEnableEndpoint(boolean enableEndpoint) { this.enableEndpoint = enableEndpoint; }

    public float getRule1MinTrailingSilence() { return rule1MinTrailingSilence; }
    public void setRule1MinTrailingSilence(float rule1MinTrailingSilence) { this.rule1MinTrailingSilence = rule1MinTrailingSilence; }

    public float getRule2MinTrailingSilence() { return rule2MinTrailingSilence; }
    public void setRule2MinTrailingSilence(float rule2MinTrailingSilence) { this.rule2MinTrailingSilence = rule2MinTrailingSilence; }

    public float getRule3MinUtteranceLength() { return rule3MinUtteranceLength; }
    public void setRule3MinUtteranceLength(float rule3MinUtteranceLength) { this.rule3MinUtteranceLength = rule3MinUtteranceLength; }

    public String getHotwordsFile() { return hotwordsFile; }
    public void setHotwordsFile(String hotwordsFile) { this.hotwordsFile = hotwordsFile; }

    public float getHotwordsScore() { return hotwordsScore; }
    public void setHotwordsScore(float hotwordsScore) { this.hotwordsScore = hotwordsScore; }
}
