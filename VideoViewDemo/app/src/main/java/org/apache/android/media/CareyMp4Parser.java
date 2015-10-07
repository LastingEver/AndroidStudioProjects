package org.apache.android.media;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.ChunkOffsetBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.SampleSizeBox;
import com.coremedia.iso.boxes.SampleTableBox;
import com.coremedia.iso.boxes.SampleToChunkBox;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.coremedia.iso.boxes.fragment.MovieExtendsBox;
import com.coremedia.iso.boxes.fragment.MovieFragmentBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentBox;
import com.coremedia.iso.boxes.fragment.TrackRunBox;
import com.googlecode.mp4parser.authoring.DateHelper;
import com.googlecode.mp4parser.authoring.TrackMetaData;

public class CareyMp4Parser {
	double lengthInSeconds;
	long[] syncSamples;
	long[] syncSamplesOffset;
	long[] syncSamplesSize;
	double[] timeOfSyncSamples;
	TrackMetaData trackMetaData = new TrackMetaData();

	public CareyMp4Parser(IsoFile isoFile) {
		lengthInSeconds = (double) isoFile.getMovieBox().getMovieHeaderBox()
				.getDuration()
				/ isoFile.getMovieBox().getMovieHeaderBox().getTimescale();

		List<TrackBox> trackBoxes = isoFile.getMovieBox().getBoxes(
				TrackBox.class);

		Iterator<TrackBox> iterator = trackBoxes.iterator();

		SampleTableBox stbl = null;
		TrackBox trackBox = null;
		while (iterator.hasNext()) {
			trackBox = iterator.next();
			stbl = trackBox.getMediaBox().getMediaInformationBox()
					.getSampleTableBox();
			// 我们只处理视频的关键帧，音频数据略过
			if (stbl.getSyncSampleBox() != null) {
				syncSamples = stbl.getSyncSampleBox().getSampleNumber();
				syncSamplesSize = new long[syncSamples.length];
				syncSamplesOffset = new long[syncSamples.length];
				timeOfSyncSamples = new double[syncSamples.length];
				break;
			}
		}

		// first we get all sample from the 'normal' MP4 part.
		// if there are none - no problem.
		SampleSizeBox sampleSizeBox = trackBox.getSampleTableBox()
				.getSampleSizeBox();
		ChunkOffsetBox chunkOffsetBox = trackBox.getSampleTableBox()
				.getChunkOffsetBox();
		SampleToChunkBox sampleToChunkBox = trackBox.getSampleTableBox()
				.getSampleToChunkBox();

		if (sampleToChunkBox != null
				&& sampleToChunkBox.getEntries().size() > 0
				&& chunkOffsetBox != null
				&& chunkOffsetBox.getChunkOffsets().length > 0
				&& sampleSizeBox != null && sampleSizeBox.getSampleCount() > 0) {
			long[] numberOfSamplesInChunk = sampleToChunkBox
					.blowup(chunkOffsetBox.getChunkOffsets().length);
			if (sampleSizeBox.getSampleSize() > 0) {
				// Every sample has the same size!
				// no need to store each size separately
				// this happens when people use raw audio formats in MP4 (are
				// you stupid guys???)
				int sampleIndex = 0;
				long sampleSize = sampleSizeBox.getSampleSize();
				for (int i = 0; i < numberOfSamplesInChunk.length; i++) {
					long thisChunksNumberOfSamples = numberOfSamplesInChunk[i];
					long sampleOffset = chunkOffsetBox.getChunkOffsets()[i];
					for (int j = 0; j < thisChunksNumberOfSamples; j++) {
						// samples always start with 1 but we start with zero
						// therefore +1
						int syncSamplesIndex = Arrays.binarySearch(syncSamples,
								sampleIndex + 1);
						if (syncSamplesIndex >= 0) {
							syncSamplesOffset[syncSamplesIndex] = sampleOffset;
							syncSamplesSize[syncSamplesIndex] = sampleSize;
						}
						sampleOffset += sampleSize;
						sampleIndex++;
					}
				}
			} else {
				// the normal case where all samples have different sizes
				int sampleIndex = 0;
				long sampleSizes[] = sampleSizeBox.getSampleSizes();
				for (int i = 0; i < numberOfSamplesInChunk.length; i++) {
					long thisChunksNumberOfSamples = numberOfSamplesInChunk[i];
					long sampleOffset = chunkOffsetBox.getChunkOffsets()[i];
					for (int j = 0; j < thisChunksNumberOfSamples; j++) {
						long sampleSize = sampleSizes[sampleIndex];
						// samples always start with 1 but we start with zero
						// therefore +1
						int syncSamplesIndex = Arrays.binarySearch(syncSamples,
								sampleIndex + 1);
						if (syncSamplesIndex >= 0) {
							syncSamplesOffset[syncSamplesIndex] = sampleOffset;
							syncSamplesSize[syncSamplesIndex] = sampleSize;
						}
						sampleOffset += sampleSize;
						sampleIndex++;
					}
				}
			}

			MediaHeaderBox mdhd = trackBox.getMediaBox().getMediaHeaderBox();
			TrackHeaderBox tkhd = trackBox.getTrackHeaderBox();

			trackMetaData.setTrackId(tkhd.getTrackId());
			trackMetaData.setCreationTime(DateHelper.convert(mdhd
					.getCreationTime()));
			trackMetaData.setLanguage(mdhd.getLanguage());
			trackMetaData.setModificationTime(DateHelper.convert(mdhd
					.getModificationTime()));
			trackMetaData.setTimescale(mdhd.getTimescale());
			trackMetaData.setHeight(tkhd.getHeight());
			trackMetaData.setWidth(tkhd.getWidth());
			trackMetaData.setLayer(tkhd.getLayer());

			List<TimeToSampleBox.Entry> decodingTimeEntries = null;
			if (trackBox.getParent().getBoxes(MovieExtendsBox.class).size() > 0) {
				decodingTimeEntries = new LinkedList<TimeToSampleBox.Entry>();
				for (MovieFragmentBox movieFragmentBox : trackBox.getIsoFile()
						.getBoxes(MovieFragmentBox.class)) {
					List<TrackFragmentBox> trafs = movieFragmentBox
							.getBoxes(TrackFragmentBox.class);
					for (TrackFragmentBox traf : trafs) {
						if (traf.getTrackFragmentHeaderBox().getTrackId() == trackBox
								.getTrackHeaderBox().getTrackId()) {
							List<TrackRunBox> truns = traf
									.getBoxes(TrackRunBox.class);
							for (TrackRunBox trun : truns) {
								for (TrackRunBox.Entry entry : trun
										.getEntries()) {
									if (trun.isSampleDurationPresent()) {
										if (decodingTimeEntries.size() == 0
												|| decodingTimeEntries.get(
														decodingTimeEntries
																.size() - 1)
														.getDelta() != entry
														.getSampleDuration()) {
											decodingTimeEntries
													.add(new TimeToSampleBox.Entry(
															1,
															entry.getSampleDuration()));
										} else {
											TimeToSampleBox.Entry e = decodingTimeEntries
													.get(decodingTimeEntries
															.size() - 1);
											e.setCount(e.getCount() + 1);
										}
									}
								}
							}
						}
					}
				}
			} else {
				decodingTimeEntries = stbl.getTimeToSampleBox().getEntries();
			}

			long currentSample = 0;
			double currentTime = 0;
			for (TimeToSampleBox.Entry entry : decodingTimeEntries) {
				for (int j = 0; j < entry.getCount(); j++) {
					int syncSamplesIndex = Arrays.binarySearch(syncSamples,
							currentSample + 1);
					if (syncSamplesIndex >= 0) {
						// samples always start with 1 but we start with zero
						// therefore +1
						timeOfSyncSamples[syncSamplesIndex] = currentTime;
					}
					currentTime += (double) entry.getDelta()
							/ (double) trackMetaData.getTimescale();
					currentSample++;
				}
			}
		}

	}

	public void printinfo() {
		System.out.println("视频总时长(秒): " + lengthInSeconds);
		System.out.println("关键帧 \t 帧偏移 \t 帧大小 \t 帧对应的时间");

		int size = syncSamples.length;

		for (int i = 0; i < size; i++) {
			System.out.println(syncSamples[i] + " " + syncSamplesOffset[i]
					+ " " + syncSamplesSize[i] + " " + timeOfSyncSamples[i]);
		}
	}
}
