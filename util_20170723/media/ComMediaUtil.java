package util.media;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;

public class ComMediaUtil {
	
	
	public static boolean mux(String videoFile, String audioFile, String outputFile) {
		Movie video;
		try {
			video = new MovieCreator().build(videoFile);
		} catch (RuntimeException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		Movie audio;
		try {
			audio = new MovieCreator().build(audioFile);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (NullPointerException e) {
			e.printStackTrace();
			return false;
		}

		Track audioTrack = audio.getTracks().get(0);
		video.addTrack(audioTrack);

		Container out = new DefaultMp4Builder().build(video);

		FileOutputStream fos;
		try {
			fos = new FileOutputStream(outputFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		BufferedWritableFileByteChannel byteBufferByteChannel = new BufferedWritableFileByteChannel(fos);
		try {
			out.writeContainer(byteBufferByteChannel);
			byteBufferByteChannel.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private static class BufferedWritableFileByteChannel implements WritableByteChannel {
		private static final int BUFFER_CAPACITY = 1000000;

		private boolean isOpen = true;
		private final OutputStream outputStream;
		private final ByteBuffer byteBuffer;
		private final byte[] rawBuffer = new byte[BUFFER_CAPACITY];

		private BufferedWritableFileByteChannel(OutputStream outputStream) {
			this.outputStream = outputStream;
			this.byteBuffer = ByteBuffer.wrap(rawBuffer);
		}

		@Override
		public int write(ByteBuffer inputBuffer) throws IOException {
			int inputBytes = inputBuffer.remaining();

			if (inputBytes > byteBuffer.remaining()) {
				dumpToFile();
				byteBuffer.clear();

				if (inputBytes > byteBuffer.remaining()) {
					throw new BufferOverflowException();
				}
			}

			byteBuffer.put(inputBuffer);

			return inputBytes;
		}

		@Override
		public boolean isOpen() {
			return isOpen;
		}

		@Override
		public void close() throws IOException {
			dumpToFile();
			isOpen = false;
		}
		private void dumpToFile() {
			try {
				outputStream.write(rawBuffer, 0, byteBuffer.position());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static void main(String[] args) {
		mux("F:\\download\\youtube\\Chinajoy SG.mp4", "F:\\download\\youtube\\Chinajoy SG.webm", "F:\\download\\youtube\\Chinajoy SG_.mp4");
	}
}
