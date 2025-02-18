package io.urdego.urdego_content_service.domain.service.model;

import io.urdego.urdego_content_service.common.exception.ExceptionMessage;
import io.urdego.urdego_content_service.common.exception.content.UserContentException;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.proto.GraphDef;
import org.tensorflow.types.TFloat32;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// 유해 컨텐츠 감지 (NSFW = Not Safe For Work)
public class NSFWDetector {

    // 모델 관련 상수
    private static final Path MODEL_PATH = Paths.get("/urdego/tensorflow/nsfw.pb");
    private static final String TENSOR_INPUT_NAME = "input_1";
    private static final String TENSOR_OUTPUT_NAME = "dense_3/Softmax";
    private static final int TENSOR_OUTPUT_FIRST_INDEX = 0;
    private static final int TENSOR_OUTPUT_CLASSES_INDEX = 1;

    // 이미지 관련 상수
    private static final int IMG_WEIGHT = 224;
    private static final int IMG_HEIGHT = 224;
    private static final double NSFW_THRESHOLD_RATIO = 0.6;
    private static final int EXPLICIT_CLASS_INDEX = 3;
    private static final int PORNO_CLASS_INDEX = 4;
    private static final int BATCH_SIZE = 1;
    private static final int BYTE_MASK = 0xFF;
    private static final int START_X = 0;
    private static final int START_Y = 0;
    private static final int BATCH_INDEX = 0;

    // 색상 관련 상수
    private static final float RGB_MAX_VALUE = 255.0f;
    private static final int RGB_CHANNELS = 3;
    private static final int RED_SHIFT = 16;
    private static final int GREEN_SHIFT = 8;
    private static final int RED_CHANNEL = 0;
    private static final int GREEN_CHANNEL = 1;
    private static final int BLUE_CHANNEL = 2;

    private static final Graph graph;

    static {
        try {
            // 모델 로드
            graph = new Graph();
            byte[] graphDef = Files.readAllBytes(MODEL_PATH);
            graph.importGraphDef(GraphDef.parseFrom(graphDef));
        } catch (IOException e) {
            throw new UserContentException(ExceptionMessage.GRAPH_LOAD_FAILED);
        }
    }

    // 이미지의 NSFW 여부 판단
    public static boolean isNSFW(byte[] imageBytes) throws IOException {
        try (Session session = new Session(graph);
             Tensor inputTensor = preprocessImage(imageBytes)) {

            try (Tensor outputTensor = session.runner()
                    .feed(TENSOR_INPUT_NAME, inputTensor)
                    .fetch(TENSOR_OUTPUT_NAME)
                    .run()
                    .get(TENSOR_OUTPUT_FIRST_INDEX)) {


                try (var rawTensor = outputTensor.asRawTensor()) {
                    // float 데이터 버퍼 가져오기
                    var floatBuffer = rawTensor.data().asFloats();
                    int outputSize = (int) outputTensor.shape().asArray()[TENSOR_OUTPUT_CLASSES_INDEX];
                    float[] probabilities = new float[outputSize];

                    // float 배열로 복사
                    floatBuffer.read(probabilities);

                    // 인덱스 3과 4의 값 확인
                    float nsfwProbabilityClass3 = probabilities[EXPLICIT_CLASS_INDEX]; // NSFW 3 (Explicit NSFW Content)
                    float nsfwProbabilityClass4 = probabilities[PORNO_CLASS_INDEX]; // NSFW 4 (Porno NSFW Content)

                    // NSFW 비율 판단
                    return nsfwProbabilityClass3 > NSFW_THRESHOLD_RATIO || nsfwProbabilityClass4 > NSFW_THRESHOLD_RATIO;
                }
            }
        }
    }

    // 이미지 전처리 Tensor 변환
    private static TFloat32 preprocessImage(byte[] imageBytes) throws IOException {
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));

        // 224x224로 리사이즈 -> 딥러닝 CNN 모델(tensorflow) 사용
        BufferedImage resized = new BufferedImage(IMG_WEIGHT, IMG_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resized.createGraphics();
        g.drawImage(img, START_X, START_Y, IMG_WEIGHT, IMG_HEIGHT, null);
        g.dispose();

        float[][][][] inputData = new float[BATCH_SIZE][IMG_WEIGHT][IMG_HEIGHT][RGB_CHANNELS];
        for (int y = 0; y < IMG_WEIGHT; y++) {
            for (int x = 0; x < IMG_HEIGHT; x++) {
                int rgb = resized.getRGB(x, y);
                inputData[BATCH_INDEX][y][x][RED_CHANNEL] = ((rgb >> RED_SHIFT) & BYTE_MASK) / RGB_MAX_VALUE; // Red
                inputData[BATCH_INDEX][y][x][GREEN_CHANNEL] = ((rgb >> GREEN_SHIFT) & BYTE_MASK) / RGB_MAX_VALUE;  // Green
                inputData[BATCH_INDEX][y][x][BLUE_CHANNEL] = (rgb & BYTE_MASK) / RGB_MAX_VALUE;         // Blue
            }
        }

        // Tensor 생성
        return TFloat32.tensorOf(org.tensorflow.ndarray.StdArrays.ndCopyOf(inputData));
    }

}