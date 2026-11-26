import java.io.ByteArrayOutputStream;

/**
 * Kafka 프로토콜의 정수 타입(big-endian)을 읽고 쓰는 유틸.
 * shift 연산과 bit mask는 이 클래스 안에서만 사용한다.
 */
public final class ByteUtils {
  private ByteUtils() {}

  public static short readInt16(byte[] buffer, int offset) {
    return (short) (((buffer[offset] & 0xFF) << 8)
        | (buffer[offset + 1] & 0xFF));
  }

  public static int readInt32(byte[] buffer, int offset) {
    return ((buffer[offset] & 0xFF) << 24)
        | ((buffer[offset + 1] & 0xFF) << 16)
        | ((buffer[offset + 2] & 0xFF) << 8)
        | (buffer[offset + 3] & 0xFF);
  }

  // ByteArrayOutputStream.write(int)는 하위 8비트만 쓰므로 mask가 필요 없다.
  public static void writeInt8(ByteArrayOutputStream out, int value) {
    out.write(value);
  }

  public static void writeInt16(ByteArrayOutputStream out, int value) {
    writeInt8(out, value >> 8);
    writeInt8(out, value);
  }

  public static void writeInt32(ByteArrayOutputStream out, int value) {
    writeInt16(out, value >> 16);
    writeInt16(out, value);
  }
}
