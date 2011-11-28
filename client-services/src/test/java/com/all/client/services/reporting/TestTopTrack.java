package com.all.client.services.reporting;

import static org.junit.Assert.*;

import org.junit.Test;

import com.all.client.model.MockTrack;
import com.all.shared.model.Track;

public class TestTopTrack {
	private TopTrack topTrack = new TopTrack(5);

	Track trackMeta1 = createMetadata(1);
	Track trackMeta2 = createMetadata(2);
	Track trackMeta3 = createMetadata(3);
	Track trackMeta4 = createMetadata(4);
	Track trackMeta5 = createMetadata(5);
	Track track1 = createNoMetadata(6);
	Track track2 = createNoMetadata(7);
	Track track3 = createNoMetadata(8);
	Track track4 = createNoMetadata(9);
	Track track5 = createNoMetadata(10);

	@Test
	public void shouldAdd5TracksWithoutMetadata() throws Exception {
		addNoMetadata();

		assertEquals(5, topTrack.getTrackCount());
		assertEquals(5, topTrack.getTracks().size());
		assertEquals(track1, topTrack.getTracks().get(0));
		assertEquals(track2, topTrack.getTracks().get(1));
		assertEquals(track3, topTrack.getTracks().get(2));
		assertEquals(track4, topTrack.getTracks().get(3));
		assertEquals(track5, topTrack.getTracks().get(4));
	}

	@Test
	public void shouldNotAddDuplicates() throws Exception {
		Track track1 = createNoMetadata(1);
		topTrack.add(track1);
		topTrack.add(track1);
		topTrack.add(track1);

		assertEquals(1, topTrack.getTrackCount());
		assertEquals(1, topTrack.getTracks().size());
		assertEquals(track1, topTrack.getTracks().get(0));
	}

	@Test
	public void shouldAdd5TracksWithMetadata() throws Exception {
		addMetadata();
		assertEquals(5, topTrack.getTrackCount());
		assertEquals(5, topTrack.getTracks().size());
		assertEquals(trackMeta1, topTrack.getTracks().get(0));
		assertEquals(trackMeta2, topTrack.getTracks().get(1));
		assertEquals(trackMeta3, topTrack.getTracks().get(2));
		assertEquals(trackMeta4, topTrack.getTracks().get(3));
		assertEquals(trackMeta5, topTrack.getTracks().get(4));
	}

	@Test
	public void shouldAddNoMetadataAndThenMetadataAndShouldReplaceNoMetadataWithMetadataOnes() throws Exception {
		shouldAdd5TracksWithoutMetadata();
		addMetadata();

		assertEquals(10, topTrack.getTrackCount());
		assertEquals(5, topTrack.getTracks().size());
		assertEquals(trackMeta1, topTrack.getTracks().get(0));
		assertEquals(trackMeta2, topTrack.getTracks().get(1));
		assertEquals(trackMeta3, topTrack.getTracks().get(2));
		assertEquals(trackMeta4, topTrack.getTracks().get(3));
		assertEquals(trackMeta5, topTrack.getTracks().get(4));
	}

	@Test
	public void shouldAddMetadataAndRefuseToReplaceMetadataTracks() throws Exception {
		shouldAdd5TracksWithMetadata();
		addNoMetadata();
		assertEquals(10, topTrack.getTrackCount());
		assertEquals(5, topTrack.getTracks().size());
		assertEquals(trackMeta1, topTrack.getTracks().get(0));
		assertEquals(trackMeta2, topTrack.getTracks().get(1));
		assertEquals(trackMeta3, topTrack.getTracks().get(2));
		assertEquals(trackMeta4, topTrack.getTracks().get(3));
		assertEquals(trackMeta5, topTrack.getTracks().get(4));
	}

	@Test
	public void shouldReplaceATrackWithMetadata() throws Exception {
		Track replace = createMetadata(20);
		addMetadata();
		for (int i = 0; i < 100; i++) {
			topTrack.add(replace);
			if (topTrack.getTracks().contains(replace)) {
				break;
			}
		}
	}

	private Track createNoMetadata(int i) {
		String s = "t" + i;
		MockTrack track = new MockTrack(null, null, null, null, null, 0, null, null, null, s, s, null, null, null, true,
				false, true, null, null, null, 0, 0, 0, 0);
		return track;
	}

	private Track createMetadata(int i) {
		String s = "t" + i;
		MockTrack track = new MockTrack(s, s, s, s, null, 0, null, null, null, s, s, null, null, null, true, false, true,
				null, null, null, 0, 0, 0, 0);
		return track;
	}

	private void addMetadata() {
		topTrack.add(trackMeta1);
		topTrack.add(trackMeta2);
		topTrack.add(trackMeta3);
		topTrack.add(trackMeta4);
		topTrack.add(trackMeta5);
	}

	private void addNoMetadata() {
		topTrack.add(track1);
		topTrack.add(track2);
		topTrack.add(track3);
		topTrack.add(track4);
		topTrack.add(track5);
	}

}
