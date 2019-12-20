package cddb4.dao;

import java.util.List;

import cddb4.model.Album;

public interface AlbumDao {
	Album findById(long id);
	
	Album findByArtistName(String artist, String name);
	
	List<Album> findAllAlbums();
	
	void saveAlbum(Album album);
	
	void deleteAlbum(Album album);
}
