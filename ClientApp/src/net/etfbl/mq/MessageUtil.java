package net.etfbl.mq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageUtil {
	private final static String QUEUE_NAME1 = "messages";
    private final static String QUEUE_NAME2 = "messages2";
    private final static String QUEUE_NAME3 = "messages3";
    private final static String QUEUE_NAME4 = "messages4";
    private final static List<String> QUEUE_NAMES = Arrays.asList(QUEUE_NAME1, QUEUE_NAME2, QUEUE_NAME3, QUEUE_NAME4);
    
    private static AtomicInteger currentIndex = new AtomicInteger(0);
	
	public static List<String> divideMessage(String message) {
        Random random = new Random();
        int M = random.nextInt(8) + 3;

        int messageLength = message.length();
        int segmentSize = messageLength / M;
        List<String> segments = new ArrayList<>();

        for (int i = 0; i < M; i++) {
            int start = i * segmentSize;
            int end = (i == M - 1) ? messageLength : (i + 1) * segmentSize;
            String segment = message.substring(start, end);
            segments.add(segment);
        }

        return segments;
    }
	
	public static String getNextQueueName() {
        int index = currentIndex.getAndUpdate(value -> (value + 1) % QUEUE_NAMES.size());
        return QUEUE_NAMES.get(index);
    }

}
