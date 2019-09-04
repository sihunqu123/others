package util.media;

import java.io.*;

import javax.media.*;
import javax.media.control.TrackControl;
import javax.media.datasink.*;
import javax.media.format.*;
import javax.media.protocol.*;

import util.commonUtil.ComLogUtil;

import java.net.*;

public class MergeAudioVideo {

	public static void main(String args[]) {
		args = new String[] {"F:\\download\\youtube\\Chinajoy SG.mp4", "F:\\download\\youtube\\Chinajoy SG.webm"}; 
		if (args.length > 1) {
			System.out.println("Please wait...");
			merging(args[0], args[1]);
			System.out.println("Merging finished");
		} else
			System.out.println("Invalid files input");
		System.exit(0);

	}
	

	/**
	 * merge the sound and video files
	 * @param audioFileName
	 * @param videoFileName
	 */
	public static void merging(String audioFileName, String videoFileName) {

		// Declare and initialize StateHelper objects: sha,shv, and shm
		// sha for audio processor, shvfor audio process, and shm for merge
		// processor
		StateHelper sha = null;
		StateHelper shv = null;
		StateHelper shm = null;

		// Declare and initialize processor objects for audio, video, and merged
		// data
		Processor audioProcessor = null;
		Processor videoProcessor = null;
		Processor mergeProcessor = null;

		// create MediaLocator objects for audio and video files
		MediaLocator audioLocator = null;
		MediaLocator videoLocator = null;
		MediaLocator outLocator = null;
		try {
			File audioFile = new File(audioFileName);
			audioLocator = new MediaLocator(audioFile.toURI().toURL());

			File videoFile = new File(videoFileName);
			videoLocator = new MediaLocator(videoFile.toURI().toURL());

			// Create MediaLocator for merged output file
			File outFile = new File(System.currentTimeMillis()
					+ "mergedvideo.mov");
			outLocator = new MediaLocator(outFile.toURI().toURL());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			ComLogUtil.info("failed");
			return;
		}

		// create datasources
		DataSource audioDataSource = null;
		DataSource videoDataSource = null;
		DataSource mergedDataSource = null;
		DataSource arrayDataSource[] = null;
		try {
			audioDataSource = Manager.createDataSource(audioLocator); // your
																		// audio
																		// file
			videoDataSource = Manager.createDataSource(videoLocator); // your
																		// video
																		// file
			mergedDataSource = null; // data source to combine video with audio
			arrayDataSource = new DataSource[2]; // data source array
		} catch (Exception e) {
			e.printStackTrace();
			ComLogUtil.info("failed");return;
		}
		// format array for input audio and video
		Format[] formats = new Format[2];
		formats[0] = new AudioFormat(AudioFormat.IMA4_MS); // create audio
															// format object
		formats[1] = new VideoFormat(VideoFormat.JPEG); // create video format
														// object

		// create media file content type object
		FileTypeDescriptor outftd = new FileTypeDescriptor(
				FileTypeDescriptor.QUICKTIME);

		// create processor objects for video and audio
		try {
			videoProcessor = Manager.createProcessor(videoDataSource);
			shv = new StateHelper(videoProcessor);
			audioProcessor = Manager.createProcessor(audioDataSource);
			sha = new StateHelper(audioProcessor);
		} catch (Exception e) {
			e.printStackTrace();
			ComLogUtil.info("failed");return;
		}

		// Configure processors
		if (!shv.configure(10000)) {
			ComLogUtil.info("failed");return;
		}
			
		if (!sha.configure(10000)) {
			ComLogUtil.info("failed");return;
		}
			
		// Realize processors

		if (!shv.realize(10000)) {
			ComLogUtil.info("failed");return;
		}
			
		if (!sha.realize(10000)) {
			ComLogUtil.info("failed");return;
		}

		// return data sources from processors so they can be merged
		arrayDataSource[0] = audioProcessor.getDataOutput();
		arrayDataSource[1] = videoProcessor.getDataOutput();

		// start the processors
		videoProcessor.start();
		audioProcessor.start();

		// create merged data source, connect, and start it
		try {
			mergedDataSource = Manager.createMergingDataSource(arrayDataSource);
			mergedDataSource.connect();
			mergedDataSource.start();
		} catch (IOException ie) {
			ComLogUtil.info("failed");return;
		} catch (IncompatibleSourceException id) {
			ComLogUtil.info("failed");return;
		}
		// processor for merged output
		try {
			mergeProcessor = Manager
					.createRealizedProcessor(new ProcessorModel(
							mergedDataSource, formats, outftd));
			shm = new StateHelper(mergeProcessor);
		} catch(Exception e) {
			e.printStackTrace();
			ComLogUtil.info("failed");return;
		}
		// set output file content type
		mergeProcessor.setContentDescriptor(new ContentDescriptor(
				FileTypeDescriptor.QUICKTIME));
		// query supported formats
		TrackControl tcs[] = mergeProcessor.getTrackControls();
		Format f[] = tcs[0].getSupportedFormats();
		if (f == null || f.length <= 0)
			System.exit(100);
		// set track format
		tcs[0].setFormat(f[0]);

		// get datasource from the mergeProcessor so it is ready to write to a
		// file by DataSink filewriter
		DataSource source = mergeProcessor.getDataOutput();
		// create DataSink filewrite for writing
		DataSink filewriter = null;
		try {
			filewriter = Manager.createDataSink(source, outLocator);
			filewriter.open();
		} catch (NoDataSinkException e) {
			System.exit(100);
		} catch (IOException e) {
			System.exit(100);
		} catch (SecurityException e) {
			System.exit(100);
		}

		// now start the filewriter and mergeProcessor
		try {
			mergeProcessor.start();
			filewriter.start();
		} catch (IOException e) {
			e.printStackTrace();
			ComLogUtil.info("failed");
			return;
		}
		// wait 2 seconds for end of media stream
		shm.waitToEndOfMedia(2000);
		shm.close();
		filewriter.close();

	}

}

// The StateHelper class help you determine the states of the processors
class StateHelper implements ControllerListener {
	Processor p = null;
	boolean configured = false;
	boolean realized = false;
	boolean prefetched = false;
	boolean eom = false;
	boolean failed = false;
	boolean closed = false;

	public StateHelper(Processor pr) {
		p = pr;
		p.addControllerListener(this);
	}

	public boolean configure(int timeOutMillis) {
		long startTime = System.currentTimeMillis();
		synchronized (this) {
			p.configure();
			while (!configured && !failed) {
				try {
					wait(timeOutMillis);
				} catch (InterruptedException ie) {
				}
				if (System.currentTimeMillis() - startTime > timeOutMillis)
					break;
			}

		}
		return configured;
	}

	public boolean realize(int timeOutMillis) {
		long startTime = System.currentTimeMillis();
		synchronized (this) {
			p.realize();
			while (!realized && !failed) {
				try {
					wait(timeOutMillis);
				} catch (InterruptedException ie) {
				}
				if (System.currentTimeMillis() - startTime > timeOutMillis)
					break;
			}
		}
		return realized;
	}

	public boolean prefetch(int timeOutMillis) {
		long startTime = System.currentTimeMillis();
		synchronized (this) {
			p.prefetch();
			while (!prefetched && !failed) {
				try {
					wait(timeOutMillis);
				} catch (InterruptedException ie) {
				}
				if (System.currentTimeMillis() - startTime > timeOutMillis)
					break;
			}
		}
		return prefetched && !failed;
	}

	public boolean waitToEndOfMedia(int timeOutMillis) {
		long startTime = System.currentTimeMillis();
		eom = false;
		synchronized (this) {
			while (!eom && !failed) {
				try {
					wait(timeOutMillis);
				} catch (InterruptedException ie) {
				}
				if (System.currentTimeMillis() - startTime > timeOutMillis)
					break;
			}
		}
		return eom && !failed;
	}

	public void close() {
		synchronized (this) {
			p.close();
			while (!closed) {
				try {
					wait(100);
				} catch (InterruptedException ie) {
				}
			}

		}
		p.removeControllerListener(this);
	}

	public synchronized void controllerUpdate(ControllerEvent ce) {
		if (ce instanceof RealizeCompleteEvent) {
			realized = true;
		} else if (ce instanceof ConfigureCompleteEvent) {
			configured = true;
		} else if (ce instanceof PrefetchCompleteEvent) {
			prefetched = true;
		} else if (ce instanceof EndOfMediaEvent) {
			eom = true;
		} else if (ce instanceof ControllerErrorEvent) {
			failed = true;
		} else if (ce instanceof ControllerClosedEvent) {
			closed = true;
		} else {
			return;
		}
		notifyAll();
	}

}
