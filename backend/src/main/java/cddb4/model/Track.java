package cddb4.model;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Component
@Table (name = "albumtracks")
public class Track {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long trackid;
	@Size (max=100)
	private String name;
	@Min(value=0)
	private int tracknr;
	
	@ManyToOne
	@JoinColumn(name = "albumid")
	@JsonIgnore
	private Album album;
	
	public Track(){}

	public long getTrackid() {
		return trackid;
	}

	public void setTrackid(long trackid) {
		this.trackid = trackid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTracknr() {
		return tracknr;
	}

	public void setTracknr(int tracknr) {
		this.tracknr = tracknr;
	}

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}
	
	@Override
	public String toString(){
		return getAlbum().toString()+" - "+getTracknr()+". "+getName();
	}
	
	//albumid-tracknr-name set as unique composite key, overriding hashCode()
	//and equals() in order to handle uniqueness checking locally	
	@Override
	public int hashCode(){
		return getAlbum()==null ? 0: (getAlbum().getId()+getTracknr()+"").hashCode();
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(!(o instanceof Track)) return false;
		Track that = (Track) o;
		return (getAlbum().getId()+getTracknr()+"").equals(that.getAlbum().getId()+that.getTracknr()+"");		
	}
	
	
}
