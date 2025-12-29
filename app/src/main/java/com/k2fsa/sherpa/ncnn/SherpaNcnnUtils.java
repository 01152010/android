package com.k2fsa.sherpa.ncnn;

public class SherpaNcnnUtils {

    public static com.k2fsa.sherpa.ncnn.FeatureExtractorConfig getFeatureExtractorConfig(float sampleRate, int featureDim) {
        return new com.k2fsa.sherpa.ncnn.FeatureExtractorConfig(sampleRate, featureDim);
    }

    public static com.k2fsa.sherpa.ncnn.DecoderConfig getDecoderConfig(String method, int numActivePaths) {
        return new com.k2fsa.sherpa.ncnn.DecoderConfig(method, numActivePaths);
    }

    public static com.k2fsa.sherpa.ncnn.ModelConfig getModelConfig(int type, boolean useGPU) {
        switch (type) {
            case 0: {
                String modelDir = "sherpa-ncnn-2022-09-30";
                return new com.k2fsa.sherpa.ncnn.ModelConfig(
                        modelDir + "/encoder_jit_trace-pnnx.ncnn.param",
                        modelDir + "/encoder_jit_trace-pnnx.ncnn.bin",
                        modelDir + "/decoder_jit_trace-pnnx.ncnn.param",
                        modelDir + "/decoder_jit_trace-pnnx.ncnn.bin",
                        modelDir + "/joiner_jit_trace-pnnx.ncnn.param",
                        modelDir + "/joiner_jit_trace-pnnx.ncnn.bin",
                        modelDir + "/tokens.txt",
                        1,
                        useGPU
                );
            }
            case 1: {
                String modelDir = "sherpa-ncnn-conv-emformer-transducer-2022-12-06";
                return new com.k2fsa.sherpa.ncnn.ModelConfig(
                        modelDir + "/encoder_jit_trace-pnnx.ncnn.int8.param",
                        modelDir + "/encoder_jit_trace-pnnx.ncnn.int8.bin",
                        modelDir + "/decoder_jit_trace-pnnx.ncnn.param",
                        modelDir + "/decoder_jit_trace-pnnx.ncnn.bin",
                        modelDir + "/joiner_jit_trace-pnnx.ncnn.int8.param",
                        modelDir + "/joiner_jit_trace-pnnx.ncnn.int8.bin",
                        modelDir + "/tokens.txt",
                        1,
                        useGPU
                );
            }
            case 2: {
                String modelDir = "sherpa-ncnn-streaming-zipformer-bilingual-zh-en-2023-02-13";
                return new com.k2fsa.sherpa.ncnn.ModelConfig(
                        modelDir + "/encoder_jit_trace-pnnx.ncnn.param",
                        modelDir + "/encoder_jit_trace-pnnx.ncnn.bin",
                        modelDir + "/decoder_jit_trace-pnnx.ncnn.param",
                        modelDir + "/decoder_jit_trace-pnnx.ncnn.bin",
                        modelDir + "/joiner_jit_trace-pnnx.ncnn.param",
                        modelDir + "/joiner_jit_trace-pnnx.ncnn.bin",
                        modelDir + "/tokens.txt",
                        1,
                        useGPU
                );
            }
            case 3: {
                String modelDir = "sherpa-ncnn-streaming-zipformer-en-2023-02-13";
                return new com.k2fsa.sherpa.ncnn.ModelConfig(
                        modelDir + "/encoder_jit_trace-pnnx.ncnn.param",
                        modelDir + "/encoder_jit_trace-pnnx.ncnn.bin",
                        modelDir + "/decoder_jit_trace-pnnx.ncnn.param",
                        modelDir + "/decoder_jit_trace-pnnx.ncnn.bin",
                        modelDir + "/joiner_jit_trace-pnnx.ncnn.param",
                        modelDir + "/joiner_jit_trace-pnnx.ncnn.bin",
                        modelDir + "/tokens.txt",
                        1,
                        useGPU
                );
            }
            case 4: {
                String modelDir = "sherpa-ncnn-streaming-zipformer-fr-2023-04-14";
                return new com.k2fsa.sherpa.ncnn.ModelConfig(
                        modelDir + "/encoder_jit_trace-pnnx.ncnn.param",
                        modelDir + "/encoder_jit_trace-pnnx.ncnn.bin",
                        modelDir + "/decoder_jit_trace-pnnx.ncnn.param",
                        modelDir + "/decoder_jit_trace-pnnx.ncnn.bin",
                        modelDir + "/joiner_jit_trace-pnnx.ncnn.param",
                        modelDir + "/joiner_jit_trace-pnnx.ncnn.bin",
                        modelDir + "/tokens.txt",
                        1,
                        useGPU
                );
            }
            case 5: {
                String modelDir = "/sdcard/xiezhu/sherpa-ncnn-streaming-zipformer-zh-14M-2023-02-23";
                return new com.k2fsa.sherpa.ncnn.ModelConfig(
                        modelDir + "/encoder_jit_trace-pnnx.ncnn.param",
                        modelDir + "/encoder_jit_trace-pnnx.ncnn.bin",
                        modelDir + "/decoder_jit_trace-pnnx.ncnn.param",
                        modelDir + "/decoder_jit_trace-pnnx.ncnn.bin",
                        modelDir + "/joiner_jit_trace-pnnx.ncnn.param",
                        modelDir + "/joiner_jit_trace-pnnx.ncnn.bin",
                        modelDir + "/tokens.txt",
                        2,
                        useGPU
                );
            }
            case 6: {
                String modelDir = "sherpa-ncnn-streaming-zipformer-small-bilingual-zh-en-2023-02-16";
                return new com.k2fsa.sherpa.ncnn.ModelConfig(
                        modelDir + "/encoder_jit_trace-pnnx.ncnn.param",
                        modelDir + "/encoder_jit_trace-pnnx.ncnn.bin",
                        modelDir + "/decoder_jit_trace-pnnx.ncnn.param",
                        modelDir + "/decoder_jit_trace-pnnx.ncnn.bin",
                        modelDir + "/joiner_jit_trace-pnnx.ncnn.param",
                        modelDir + "/joiner_jit_trace-pnnx.ncnn.bin",
                        modelDir + "/tokens.txt",
                        2,
                        useGPU
                );
            }
            default:
                return null;
        }
    }
}
