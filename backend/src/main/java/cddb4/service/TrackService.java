package cddb4.service;

import java.util.List;

import cddb4.model.Album;
import cddb4.model.Track;

public interface TrackService {
	Track findById(long id);
	
	Track findByAlbumNr(Album album, int tracknr);
	
	List<Track> findAllTracks();
	
	List<Track> findAlbumTracks(Album album);
	
	void saveTrack(Track track);
	
	void updateTrack(Track track);
	
	void deleteTrack(Track track);
	
	boolean trackExists (long id);
	
	boolean trackUnique(Track track);
}