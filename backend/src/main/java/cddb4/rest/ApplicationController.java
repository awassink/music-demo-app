package cddb4.rest;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import cddb4.model.Album;
import cddb4.model.Track;
import cddb4.service.AlbumService;
import cddb4.service.TrackService;

@RestController
@RequestMapping(value = "/rest/*")
public class ApplicationController {
	
	@Autowired
	AlbumService albumService;
	
	@Autowired
	TrackService trackService;
	
	//checks whether an album with id albumid already exists in the persistence layer
	private void albumExists(long albumid){
		if(!albumService.albumExists(albumid)) throw new AlbumNotFoundException(albumid);		
	}
	
	private void trackExists(long trackid){
		if(!trackService.trackExists(trackid)) throw new TrackNotFoundException(trackid);
	}
	
	//GET (all) 
	@RequestMapping(value = "/" , method=RequestMethod.GET)
	public List<Album> getAlbums(){
		return albumService.findAllAlbums();
	}
	
	//GET (one by id) if the album exists
	@RequestMapping(value = "/{albumid}", method = RequestMethod.GET)
	public Album getAlbum(@PathVariable long albumid){
		albumExists(albumid);
		return albumService.findById(albumid);
	}
	
	//POST (add new)
	//add a new Album, checks whether json input of client is valid and whether the album
	//is unique (artist-name) and if not throws an exception that results in a http 400 error.
	//Returns the newly assigned ID of the saved album when successful.
	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public long addAlbum(@RequestBody @Valid Album album, BindingResult result){
		if(result.hasErrors()) throw new InvalidInputException("New album contains invalid field \""+result.getFieldError().getField()+"\".");
		if(!albumService.albumUnique(album)) throw new InvalidInputException("New album not unique");
		albumService.saveAlbum(album);
		return album.getId();
	}
	
	//PUT (update by id), checks whether client json input is valid, whether album with the same id exists
	//and whether the new album has a unique artist-name combination that the OTHER albums don't have yet.
	@RequestMapping(value = "/{albumid}", method = RequestMethod.PUT)
	@ResponseStatus(value = HttpStatus.OK)
	public void updateAlbum(@RequestBody @Valid Album album, BindingResult result){
		if(result.hasErrors()) throw new InvalidInputException("Updated album contains invalid field \""+result.getFieldError().getField()+"\".");
		albumExists(album.getId());
		if(!albumService.albumUnique(album) &&
		   !album.equals(albumService.findById(album.getId()))) throw new InvalidInputException("Updated album not unique");
		albumService.updateAlbum(album);
	}
	
	//DELETE (by id) if the album exists, deletes all tracks as well
	@RequestMapping(value = "/{albumid}", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public void deleteAlbum(@PathVariable long albumid){
		albumExists(albumid);
		albumService.deleteAlbum(albumService.findById(albumid));
	}
	
	/**
	 * Down below are all the track calls, all of which have to be part of an album in the URL 
	 * For example localhost:8080/cddb4/albums/3/new POST adds a new track to album with id 3
	 * There will be no method to display all tracks independent of an album
	 */
	
	//GET (all by albumid) if the album exists
	@RequestMapping(value = "/{albumid}/tracks", method = RequestMethod.GET)
	public List<Track> getAlbumTracks(@PathVariable long albumid){
		albumExists(albumid);
		return trackService.findAlbumTracks(albumService.findById(albumid));
	}
	
	//GET (one by id) if the track exists
	@RequestMapping(value = "/*/tracks/{trackid}", method = RequestMethod.GET)
	public Track getTrack(@PathVariable long trackid){
		trackExists(trackid);
		return trackService.findById(trackid);
	}
	
	//DELETE (by id) if the track exists
	@RequestMapping(value = "/*/tracks/{trackid}", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public void deleteTrack(@PathVariable long trackid){
		trackExists(trackid);
		trackService.deleteTrack(trackService.findById(trackid));
	}
	
	//POST (add new) checks for album existence, album-tracknr uniqueness and input validity
	@RequestMapping(value = "/{albumid}/tracks/", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public long addTrack(@RequestBody @Valid Track track, BindingResult result, @PathVariable long albumid){
		albumExists(albumid);
		track.setAlbum(albumService.findById(albumid));
		if(result.hasErrors()) throw new InvalidInputException("New track contains invalid field \""+result.getFieldError().getField()+"\".");
		if(!trackService.trackUnique(track)) throw new InvalidInputException("New track not unique");		
		trackService.saveTrack(track);
		return track.getTrackid();
	}
	
	//PUT (update by id) checks for album existence, album-tracknr uniqueness and input validity
	@RequestMapping(value = "/{albumid}/tracks/{trackid}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public void updateTrack(@RequestBody @Valid Track track, BindingResult result, @PathVariable long albumid){
		albumExists(albumid);
		track.setAlbum(albumService.findById(albumid));
		if(result.hasErrors()) throw new InvalidInputException("Updated track contains invalid field \""+result.getFieldError().getField()+"\".");
		if(!trackService.trackUnique(track) &&
		   !track.equals(trackService.findById(track.getTrackid()))) throw new InvalidInputException("Updated track not unique");		
		trackService.updateTrack(track);
	}
}

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Could not find album.") //404
class AlbumNotFoundException extends RuntimeException{
	public AlbumNotFoundException(long albumid){
		super("Could not find album "+albumid+".");
		System.out.println("Could not find album "+albumid+".");		
	}
}

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Could not find track.") //404
class TrackNotFoundException extends RuntimeException{
	public TrackNotFoundException(long trackid){
		super("Could not find track "+trackid+".");
		System.out.println("Could not find track "+trackid+".");		
	}
}

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Input invalid.") //400
class InvalidInputException extends RuntimeException{
	public InvalidInputException(String message){		
		super(message);
		System.out.println(message);
	}
}


