package com.example.playlistgenerator.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "Album")
@Table(name = "albums")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Album {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "album_id")
    private long id;

    @Column(name = "album_title")
    private String title;

    @Column(name = "album_tracklist_url")
    private String tracklist;
}
