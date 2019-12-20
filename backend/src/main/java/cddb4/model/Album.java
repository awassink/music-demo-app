package cddb4.model;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

/* Bean class using JPA annotations for use with Hibernate/ORM
 * artis-name combo set as unique composite key in mysql, overrides hashCode() and equals() to cope
 * id is primary key, auto-incremented in mysql.
 */

@Entity						//marks it as a database model
@Component					//enables its automatic injection as a scanned component by Spring
@Table( name = "album" )
public class Album {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@NotNull @Size(max=100)
	private String name;
	@NotNull @Size(max=100)
	private String artist;
	@Min(value=0) @Max(value=3000) @NotNull
	private int year;
	
	@JsonIgnore	//don't package the track set into json representations of the object
	@OneToMany(mappedBy="album", cascade=CascadeType.ALL, fetch=FetchType.LAZY) //tracks control the relationship, but cannot exist independently
	private Set<Track> tracks = new HashSet<Track>();
	
	public Album(){
		
	}
	
	public long getId(){
		return this.id;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getArtist(){
		return this.artist;
	}
	
	public int getYear(){
		return this.year;
	}
	
	public void setId(long value){
		this.id = value;
	}
	
	public void setName(String value){
		this.name = value;
	}
	
	public void setArtist(String value){
		this.artist = value;
	}
	
	public void setYear(int value){
		this.year = value;
	}
	
	public Set<Track> getTracks(){
		return tracks;
	}
	
	public void setTracks(Set<Track> value){
		this.tracks = value;
	}
	
	@Override
	public String toString(){
		return getName()+" ("+getYear()+") by "+getArtist();
	}
	
	//artist-name set as unique composite key, overriding hashCode()
	//and equals() in order to handle uniqueness checking locally
	@Override
	public int hashCode(){
		return (getArtist()+getName()).hashCode();
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(!(o instanceof Album)) return false;
		Album that = (Album) o;
		return (this.getArtist()+this.getName()).equals(that.getArtist()+that.getName());		
	}
}
