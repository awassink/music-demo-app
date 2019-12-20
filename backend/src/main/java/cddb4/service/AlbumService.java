package cddb4.service;

import java.util.List;

import cddb4.model.Album;

public interface AlbumService {
	Album findById(long id);
	
	Album findByArtistName(String artist, String name);
	
	List<Album> findAllAlbums();
	
	void saveAlbum(Album album);
	
	void updateAlbum(Album album);
	
	void deleteAlbum(Album album);
	
	boolean albumExists (long id);
	
	boolean albumUnique(Album album);
}
