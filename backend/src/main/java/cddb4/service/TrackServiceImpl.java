package cddb4.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cddb4.dao.TrackDao;
import cddb4.model.Album;
import cddb4.model.Track;

@Service("trackService")
@Transactional
public class TrackServiceImpl implements TrackService {
	
	@Autowired
	private TrackDao dao;
	
	@Override
	public Track findById(long id) {
		return dao.findById(id);
	}

	@Override
	public Track findByAlbumNr(Album album, int tracknr) {
		return dao.findByAlbumNr(album, tracknr);
	}

	@Override
	public List<Track> findAllTracks() {
		return dao.findAllTracks();
	}
	
	@Override
	public List<Track> findAlbumTracks(Album album){
		return dao.findAlbumTracks(album);
	}

	@Override
	public void saveTrack(Track track) {
		track.setTrackid(0); //prevents errors, see AlbumServiceImpl
		dao.saveTrack(track);
	}

	@Override
	public void updateTrack(Track track) {
		Track oldTrack = dao.findById(track.getTrackid());
		if(oldTrack != null){
			oldTrack.setTracknr(track.getTracknr());
			oldTrack.setName(track.getName());
			oldTrack.setAlbum(track.getAlbum()); //implemented for completeness, moving a track between two albums won't be possible
		}
	}

	@Override
	public void deleteTrack(Track track) {
		dao.deleteTrack(track);
	}

	@Override
	public boolean trackExists(long trackid) {
		return findById(trackid) != null;
	}

	@Override
	public boolean trackUnique(Track track) {
		return findByAlbumNr(track.getAlbum(), track.getTracknr()) == null;
	}

}
