package cddb4.dao;

import java.util.List;

import cddb4.model.Album;
import cddb4.model.Track;

public interface TrackDao {
	Track findById(long id);
	
	Track findByAlbumNr(Album album, int tracknr);
	
	List<Track> findAllTracks();
	
	List<Track> findAlbumTracks(Album album);
	
	void saveTrack(Track track);
	
	void deleteTrack(Track track);
}

