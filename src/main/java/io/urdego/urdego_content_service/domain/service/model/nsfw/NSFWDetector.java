package io.urdego.urdego_content_service.domain.service.model.nsfw;

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

    private static final Path MODEL_PATH = Paths.get("src/main/resources/nsfw.pb");
    private static final String INPUT_TENSOR = "input_1";
    private static final String OUTPUT_TENSOR = "dense_3/Softmax";
    private static final double NSFW_RATIO = 0.6;
    private static final Graph graph;

    static {
        try {
            graph = new Graph();
            byte[] graphDef = Files.readAllBytes(MODEL_PATH);
            graph.importGraphDef(GraphDef.parseFrom(graphDef));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load GraphDef", e);
        }
    }


    public static boolean isNSFW(byte[] imageBytes) throws IOException {
        try (Session session = new Session(graph);
             Tensor inputTensor = preprocessImage(imageBytes)) {

            try (Tensor outputTensor = session.runner()
                    .feed(INPUT_TENSOR, inputTensor) // 입력 텐서 이름
                    .fetch(OUTPUT_TENSOR)    // 출력 텐서 이름
                    .run()
                    .get(0)) {


                try (var rawTensor = outputTensor.asRawTensor()) {
                    // float 데이터 버퍼 가져오기
                    var floatBuffer = rawTensor.data().asFloats();
                    int outputSize = (int) outputTensor.shape().asArray()[1]; // 출력 클래스 개수 확인
                    float[] probabilities = new float[outputSize];

                    // float 배열로 복사
                    floatBuffer.read(probabilities);

                    // 인덱스 3과 4의 값 확인
                    float nsfwProbabilityClass3 = probabilities[3]; // NSFW 3 (Explicit NSFW Content)
                    float nsfwProbabilityClass4 = probabilities[4]; // NSFW 4 (Porno NSFW Content)

                    // NSFW 여부 판단
                    return nsfwProbabilityClass3 > NSFW_RATIO || nsfwProbabilityClass4 > NSFW_RATIO;
                }
            }
        }
    }

    private static TFloat32 preprocessImage(byte[] imageBytes) throws IOException {
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));

        // 224x224로 리사이즈
        BufferedImage resized = new BufferedImage(224, 224, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resized.createGraphics();
        g.drawImage(img, 0, 0, 224, 224, null);
        g.dispose();

        // [0, 1] 범위로 정규화된 float 배열 생성
        float[][][][] inputData = new float[1][224][224][3];
        for (int y = 0; y < 224; y++) {
            for (int x = 0; x < 224; x++) {
                int rgb = resized.getRGB(x, y);
                inputData[0][y][x][0] = ((rgb >> 16) & 0xFF) / 255.0f; // Red
                inputData[0][y][x][1] = ((rgb >> 8) & 0xFF) / 255.0f;  // Green
                inputData[0][y][x][2] = (rgb & 0xFF) / 255.0f;         // Blue
            }
        }

        // Tensor 생성
        return TFloat32.tensorOf(org.tensorflow.ndarray.StdArrays.ndCopyOf(inputData));
    }

}