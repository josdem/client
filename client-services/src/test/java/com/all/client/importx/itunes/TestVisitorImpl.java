package com.all.client.importx.itunes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.all.client.importx.itunes.xml.Visitor;
import com.all.client.importx.itunes.xml.VisitorFactory;
import com.all.client.importx.itunes.xml.dto.XmlTrack;
import com.all.client.importx.itunes.xml.legacy.VisitorImpl;
import com.all.client.model.LocalModelDao;
import com.all.shared.model.Track;

public class TestVisitorImpl  {
	private Log log = LogFactory.getLog(this.getClass());
	@Autowired
	private VisitorFactory visitorFactory;
	@Autowired
	private LocalModelDao localModelDao;
	
	//TODO this test a workaround int BasicStrategy, so it should be moved to TestBasicStrategy (not done because time over)
	@Ignore//NO BORRAR ESTA PRUEBA, ES NECESARIO PONER LA VARIABLE -Djava.library.path=src/main/os/windows/native EN TEST
	@Test
	public void shouldImportAiff(){
		assertNotNull( visitorFactory );
		Visitor v = visitorFactory.newInstance();
		assertTrue(v instanceof VisitorImpl);
		VisitorImpl vv = (VisitorImpl)v;
		XmlTrack x = new XmlTrack();
		x.setAlbum("album 1");
		final String FILE_NAME = "daughter.aiff";
		x.setLocation("file://localhost:src/test/resources/audioFiles1/"+FILE_NAME);
		x.setName("Tu");
		x.setPodcast(false);
		x.setTrackId("12");
		vv.visit(x);
		List<Track> trackList = localModelDao.findAll(Track.class);
		assertNotNull(trackList);
		assertEquals(1, trackList.size());
		Track t = trackList.get(0);
		assertNotNull(t);
		assertEquals(FILE_NAME,t.getFileName());
		assertTrue(t.getDuration() > 0);
		log.debug(t.getDurationMinutes());
	}

	//TODO this test a workaround int BasicStrategy, so it should be moved to TestBasicStrategy (not done because time over)
	@Ignore//NO BORRAR ESTA PRUEBA, ES NECESARIO PONER LA VARIABLE -Djava.library.path=src/main/os/windows/native EN TEST
	@Test
	public void shouldImportAu(){
		assertNotNull( visitorFactory );
		Visitor v = visitorFactory.newInstance();
		assertTrue(v instanceof VisitorImpl);
		VisitorImpl vv = (VisitorImpl)v;
		XmlTrack x = new XmlTrack();
		x.setAlbum("album 1");
		final String FILE_NAME = "Lee_Brauer.au";
		x.setLocation("file://localhost:src/test/resources/audioFiles1/"+FILE_NAME);
		x.setName("Tu");
		x.setPodcast(false);
		x.setTrackId("13");
		vv.visit(x);
		List<Track> trackList = localModelDao.findAll(Track.class);
		assertNotNull(trackList);
		assertEquals(1, trackList.size());
		Track t = trackList.get(0);
		assertNotNull(t);
		assertEquals(FILE_NAME,t.getFileName());
		assertTrue(t.getDuration() > 0);
		log.debug(t.getDurationMinutes());
	}
	
	/**
	 * not supported now AMR
	 */
	@Ignore
	@Test
	public void shouldImportAmr(){
		assertNotNull( visitorFactory );
		Visitor v = visitorFactory.newInstance();
		assertTrue(v instanceof VisitorImpl);
		VisitorImpl vv = (VisitorImpl)v;
		XmlTrack x = new XmlTrack();
		x.setAlbum("album 1");
		final String FILE_NAME = "tu.amr";
//		x.setLocation("file://localhost:src/test/resources/audioFiles1/"+FILE_NAME);
		x.setLocation("file://localhost:src/test/resources/audioFiles1/tu.amr");
		x.setName("Tu");
		x.setPodcast(false);
		x.setTrackId("11");
		vv.visit(x);
		List<Track> trackList = localModelDao.findAll(Track.class);
		assertNotNull(trackList);
		assertEquals(1, trackList.size());
		Track t = trackList.get(0);
		assertNotNull(t);
		assertEquals(FILE_NAME,t.getFileName());
		assertTrue(t.getDuration() > 0);
		log.debug(t.getDurationMinutes());
	}
}
