package cddb4.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import cddb4.model.Album;
import cddb4.model.Track;

@Repository("trackDao")
public class TrackDaoImpl extends AbstractDao<Long, Track> implements TrackDao {

	@Override
	public Track findById(long id) {
		return getByKey(id);
	}

	@Override
	public Track findByAlbumNr(Album album, int tracknr) {
		Criteria criteria = createEntityCriteria();
		criteria.add(Restrictions.eq("album", album));
		criteria.add(Restrictions.eq("tracknr", tracknr));
		return (Track) criteria.uniqueResult();
	}

	@Override
	public List<Track> findAllTracks() {
		Criteria criteria = createEntityCriteria();
		return (List<Track>) criteria.list();
	}
	
	@Override
	public List<Track> findAlbumTracks(Album album){
		Criteria criteria = createEntityCriteria();
		criteria.add(Restrictions.eq("album", album));
		return (List<Track>) criteria.list();
	}

	@Override
	public void saveTrack(Track track) {
		persist(track);

	}

	@Override
	public void deleteTrack(Track track) {
		delete(track);

	}

}
