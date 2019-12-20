package cddb4.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import cddb4.model.Album;

@Repository("albumDao")
public class AlbumDaoImpl extends AbstractDao<Long, Album> implements AlbumDao {

	@Override
	public Album findById(long id) {
		return getByKey(id);
	}

	@Override
	public Album findByArtistName(String artist, String name) {
		Criteria criteria = createEntityCriteria();
		criteria.add(Restrictions.eq("name", name));
		criteria.add(Restrictions.eq("artist", artist));
		return (Album) criteria.uniqueResult();
	}

	@Override
	public List<Album> findAllAlbums() {
		Criteria criteria = createEntityCriteria();
		return (List<Album>) criteria.list();
	}

	@Override
	public void saveAlbum(Album album) {
		persist(album);
	}

	@Override
	public void deleteAlbum(Album album) {
		delete(album);
	}

}
