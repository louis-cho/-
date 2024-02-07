package com.ssafy.server.song.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity(name = "song_info")
public class SongInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "song_id")
    private int songId;
    @Column(name = "length")
    private int length;
    @Column(name = "genre")
    private String genre;
    @Column(name = "mml_data")
    private String mmlData;
    @Column(name = "song_url")
    private String songUrl;
    @Column(name = "album_cover")
    private String albumCover;

}
