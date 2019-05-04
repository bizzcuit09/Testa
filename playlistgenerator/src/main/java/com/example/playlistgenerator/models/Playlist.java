package com.example.playlistgenerator.models;

import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "Playlist")
@Table(name = "playlists")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Playlist {

    @Id
    @Column(name="playlist_id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    @Column(name = "playlist_title")
    private String playlistTitle;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(
            name = "track_to_playlist",
            joinColumns = @JoinColumn(name = "playlist_id"),
            inverseJoinColumns = @JoinColumn(name = "track_id")
    )
    private Set<Track> tracklist = new HashSet<>();

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(
            name = "genre_to_playlist",
            joinColumns = @JoinColumn(name = "playlist_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    public int getPlaylistDuration() {
        int playlistDuration = 0;
        for (Track track : tracklist) {
            playlistDuration += track.getDuration();
        }

        return playlistDuration;
    }

    public void addTracks(Set<Track> tracks){
        this.tracklist.addAll(tracks);
    }

    public void addGenre(Genre genre){
        this.genres.add(genre);
    }
}
