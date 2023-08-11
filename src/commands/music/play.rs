extern crate google_youtube3;
extern crate hyper;
extern crate hyper_rustls;

use poise::serenity_prelude::Colour;

use crate::{
    types::{Context, Error},
    voice::UserDisconnectNotifier,
};

use itertools::concat;
use rspotify::{
    model::{AlbumId, ArtistId, PlayableItem, PlaylistId, SimplifiedTrack, TrackId},
    prelude::*,
    ClientCredsSpotify, Credentials,
};
use songbird::{input::error::Result as SongbirdResult, input::Restartable, Event, TrackEvent};

use fancy_regex::Regex as FancyRegex;
use lazy_static::lazy_static;
use regex::Regex;

lazy_static! {

    static ref YOUTUBE_REGEX: FancyRegex = FancyRegex::new(
        r#"(?:https?:)?(?:\/\/)?(?:[0-9A-Z-]+\.)?(?:youtu\.be\/|youtube(?:-nocookie)?\.com\S*?[^\w\s-])([\w-]{11})(?=[^\w-]|$)(?![?=&+%\w.-]*(?:['"][^<>]*>|<\/a>))[?=&+%\w.-]*"#,
    )
    .expect("Failed to compile youtube regex");

    static ref YOUTUBE_PLAYLIST : Regex = Regex::new(r"^.*(youtu.be\/|list=)([^#\&\?]*).*").expect("Failed to compile youtube playlist regex");

    static ref SPOTIFY_REGEX : Regex = Regex::new(r"(https?:\/\/)(www.)?open.spotify.com\/((?<region>[a-zA-Z-]+)\/)?(user\/(?<user>[a-zA-Z0-9-_]+)\/)?(?<type>track|album|playlist|artist)\/(?<identifier>[a-zA-Z0-9-_]+)").expect("Failed to compile spotify regex");
}

/// Make RedBot play a song
#[poise::command(slash_command, prefix_command, guild_only)]
pub async fn play(
    ctx: Context<'_>,
    #[description = "A song URL or search query"] query: String,
) -> Result<(), Error> {
    ctx.defer().await?;
    let guild = ctx
        .guild()
        .ok_or_else(|| "This command can only be used in a guild".to_string())?;

    let channel_id = guild
        .voice_states
        .get(&ctx.author().id)
        .and_then(|vs| vs.channel_id);

    let manager = songbird::get(ctx.serenity_context())
        .await
        .ok_or_else(|| "Songbird client not initialized".to_string())?;

    let handler_lock = if let Some(handler_lock_) = manager.get(guild.id) {
        handler_lock_
    } else if let Some(channel_id) = channel_id {
        let (handler_lock_, success) = manager.join(guild.id, channel_id).await;

        match success {
            Ok(_channel) => {
                let send_http = ctx.serenity_context().http.clone();
                let mut handle = handler_lock_.lock().await;

                handle.add_global_event(
                    Event::Track(TrackEvent::End),
                    UserDisconnectNotifier {
                        voice_channel_id: channel_id.clone(),
                        text_channel_id: ctx.channel_id(),
                        http: send_http,
                        cache: ctx.serenity_context().cache.clone(),
                    },
                );

                handler_lock_.clone()
            }
            Err(err) => {
                return Err(format!("Failed to join the voice channel: {:?}", err).into());
            }
        }
    } else {
        return Err("You are not connected to a voice channel"
            .to_string()
            .into());
    };

    let mut handler = handler_lock.lock().await;

    let sources: [SongbirdResult<Restartable>; 1] =
        if YOUTUBE_REGEX.is_match(&query).unwrap_or(false) {
            [Restartable::ytdl(query, true).await].try_into().unwrap()
        } else if let Some(_matcher) = YOUTUBE_PLAYLIST.captures(&query) {
            //let id = matcher.get(2).unwrap().as_str().to_string();

            /*   let secret = oauth2::ApplicationSecret {
                client_id: std::env::var("GOOGLE_CLIENT_ID").expect("Missing GOOGLE_CLIENT_ID"),
                client_secret: std::env::var("GOOGLE_CLIENT_SECRET")
                    .expect("Missing GOOGLE_CLIENT_SECRET"),
                project_id: Some(
                    std::env::var("GOOGLE_PROJECT_ID").expect("Missing GOOGLE_PROJECT_ID"),
                ),
                auth_uri: "https://accounts.google.com/o/oauth2/auth".to_string(),
                token_uri: "https://oauth2.googleapis.com/token".to_string(),
                auth_provider_x509_cert_url: Some(
                    "https://www.googleapis.com/oauth2/v1/certs".to_string(),
                ),
                redirect_uris: vec!["http://localhost".to_string()],
                ..Default::default()
            };

            let auth = oauth2::InstalledFlowAuthenticator::builder(
                secret,
                oauth2::InstalledFlowReturnMethod::HTTPRedirect,
            )
            .build()
            .await
            .unwrap();*/

            /*     let auth = oauth2::AccessTokenAuthenticator::builder(
                std::env::var("GOOGLE_API_KEY").expect("Missing GOOGLE_API_KEY"),
            )
            .build()
            .await
            .unwrap();

            let hub = YouTube::new(
                hyper::Client::builder().build(
                    hyper_rustls::HttpsConnectorBuilder::new()
                        .with_native_roots()
                        .https_or_http()
                        .enable_http1()
                        .enable_http2()
                        .build(),
                ),
                auth,
            );

            let results = hub
                .playlist_items()
                .list(&vec!["snippet".to_string()])
                .playlist_id(&id)
                .max_results(50)
                .doit()
                .await
                .expect("Failed to fetch playlist items");

            let items = results.1.items.unwrap();
            let sources = items.into_iter().map(|item| async move {
                Restartable::ytdl_search(
                    format!(
                        "https://www.youtube.com/watch?v={}",
                        item.snippet.unwrap().resource_id.unwrap().video_id.unwrap()
                    ),
                    true,
                )
                .await
            });

            futures::future::join_all(sources).await.try_into().unwrap()*/
            return Err("Playlists are not supported yet".to_string().into());
        } else if let Some(matcher) = SPOTIFY_REGEX.captures(&query) {
            let creds = Credentials::from_env().unwrap();
            let spotify = ClientCredsSpotify::new(creds);
            spotify.request_token().await.unwrap();

            let link_type = matcher.name("type").unwrap().as_str();
            let id = matcher.name("identifier").unwrap().as_str().to_string();

            match link_type {
                "album" => {
                    let album = spotify
                        .album(AlbumId::from_id(id).expect("The provided Spotify link is invalid!"))
                        .await
                        .unwrap();
                    let tracks = album.tracks.items;

                    let sources = tracks.iter().map(|track| async move {
                        Restartable::ytdl_search(
                            format!(
                                "{} - {}",
                                track.name,
                                track
                                    .artists
                                    .clone()
                                    .into_iter()
                                    .map(|artist| artist.name)
                                    .collect::<Vec<_>>()
                                    .join(", ")
                            ),
                            true,
                        )
                        .await
                    });

                    futures::future::join_all(sources).await.try_into().unwrap()
                }
                "playlist" => {
                    let playlist = spotify
                        .playlist(
                            PlaylistId::from_id(id).expect("The provided Spotify link is invalid!"),
                            None, //Some("items(track(name,artists(name)))"),
                            None,
                        )
                        .await
                        .unwrap();
                    let tracks = playlist.tracks.items;

                    let sources = tracks.iter().map(|track| async move {
                        let track = track.clone().track.unwrap();

                        match track {
                            PlayableItem::Track(track) => Restartable::ytdl_search(
                                format!(
                                    "{} - {}",
                                    track.name,
                                    track
                                        .artists
                                        .into_iter()
                                        .map(|artist| artist.name)
                                        .collect::<Vec<_>>()
                                        .join(", ")
                                ),
                                true,
                            ),
                            PlayableItem::Episode(episode) => Restartable::ytdl_search(
                                format!("{} - {}", episode.name, episode.show.name),
                                true,
                            ),
                        }
                        .await
                    });

                    futures::future::join_all(sources).await.try_into().unwrap()
                }
                "artist" => {
                    let albums = spotify
                        .artist_albums_manual(
                            ArtistId::from_id(id).expect("The provided Spotify link is invalid!"),
                            None,
                            None,
                            None,
                            None,
                        )
                        .await
                        .unwrap();

                    let tracks: Vec<SimplifiedTrack> = concat(
                        futures::future::join_all(albums.items.into_iter().map(|album| async {
                            let album = spotify.album(album.id.unwrap()).await.unwrap();
                            album.tracks.items
                        }))
                        .await,
                    );

                    let sources = tracks.iter().map(|track| async move {
                        Restartable::ytdl_search(
                            format!(
                                "{} - {}",
                                track.name,
                                track
                                    .artists
                                    .clone()
                                    .into_iter()
                                    .map(|artist| artist.name)
                                    .collect::<Vec<_>>()
                                    .join(", ")
                            ),
                            true,
                        )
                        .await
                    });

                    futures::future::join_all(sources).await.try_into().unwrap()
                }
                "track" => {
                    let track = spotify
                        .track(TrackId::from_id(id).expect("The provided Spotify link is invalid!"))
                        .await
                        .unwrap();
                    [Restartable::ytdl_search(
                        format!(
                            "{} - {}",
                            track.name,
                            track
                                .artists
                                .into_iter()
                                .map(|artist| artist.name)
                                .collect::<Vec<_>>()
                                .join(", ")
                        ),
                        true,
                    )
                    .await]
                    .try_into()
                    .unwrap()
                }
                _ => {
                    return Err("The provided Spotify link is invalid!".to_string().into());
                }
            }
        } else {
            [Restartable::ytdl_search(query, true).await]
        };

    let mut i = 0;

    for source in sources {
        match source {
            Ok(source) => {
                handler.enqueue_source(source.into());
                i += 1;
            }
            Err(err) => {
                ctx.send(|m| {
                    m.embed(|e| {
                        e.author(|a| {
                            a.name("An error occured while trying to play the song")
                                .icon_url(
                                    ctx.serenity_context()
                                        .cache
                                        .current_user()
                                        .avatar_url()
                                        .unwrap_or_default(),
                                )
                        })
                        .description(format!("```{:?}```", err))
                        .color(Colour::RED)
                        .footer(|f| {
                            if let Some(user) = ctx.serenity_context().cache.user(
                                std::env::var("AUTHOR_ID")
                                    .expect("missing AUTHOR_ID")
                                    .parse::<u64>()
                                    .unwrap(),
                            ) {
                                f.icon_url(user.avatar_url().unwrap_or_default());
                            }
                            f.text("RedBot by RedBoxing")
                        })
                    })
                })
                .await?;
            }
        };
    }

    if i > 0 {
        ctx.send(|m| {
            m.embed(|e| {
                e.author(|a| {
                    a.name(format!("{} titles were added to the queue", i))
                        .icon_url(
                            ctx.serenity_context()
                                .cache
                                .current_user()
                                .avatar_url()
                                .unwrap_or_default(),
                        )
                })
                .description(format!(
                    "**Current queue** ({} elements):\n```{}```",
                    handler.queue().len(),
                    handler
                        .queue()
                        .current_queue()
                        .into_iter()
                        .map(|handle| {
                            let metadata = handle.metadata().clone();
                            format!(
                                "{} - {}",
                                metadata.title.unwrap_or_default(),
                                metadata.artist.unwrap_or_default()
                            )
                        })
                        .collect::<Vec<_>>()
                        .join("\n"),
                ))
                .color(Colour::FOOYOO)
                .footer(|f| {
                    if let Some(user) = ctx.serenity_context().cache.user(
                        std::env::var("AUTHOR_ID")
                            .expect("missing AUTHOR_ID")
                            .parse::<u64>()
                            .unwrap(),
                    ) {
                        f.icon_url(user.avatar_url().unwrap_or_default());
                    }
                    f.text("RedBot by RedBoxing")
                })
            })
        })
        .await?;
    }

    Ok(())
}
