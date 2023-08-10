use poise::serenity_prelude as serenity;
use serenity::Colour;
use songbird::{Event, TrackEvent};

use crate::types::{Context, Error};

use crate::voice::UserDisconnectNotifier;

/// Make RedBot join the voice channel you are in
#[poise::command(slash_command, prefix_command, track_edits, guild_only)]
pub async fn join(ctx: Context<'_>) -> Result<(), Error> {
    let guild = ctx
        .guild()
        .ok_or_else(|| "This command can only be used in a guild".to_string())?;

    let guild_id = guild.id;
    let channel_id = guild
        .voice_states
        .get(&ctx.author().id)
        .and_then(|vs| vs.channel_id);

    if let Some(channel_id) = channel_id {
        let manager = songbird::get(ctx.serenity_context())
            .await
            .ok_or_else(|| "Songbird client not initialized".to_string())?;

        let (handle_lock, success) = manager.join(guild_id.clone(), channel_id).await;

        match success {
            Ok(_channel) => {
                ctx.send(|m| {
                    m.embed(|e| {
                        e.author(|a| {
                            a.name("RedBot is here!").icon_url(
                                ctx.serenity_context()
                                    .cache
                                    .current_user()
                                    .avatar_url()
                                    .unwrap_or_default(),
                            )
                        })
                        .description("RedBot is now connected to your voice channel")
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

                let send_http = ctx.serenity_context().http.clone();
                let mut handle = handle_lock.lock().await;

                handle.add_global_event(
                    Event::Track(TrackEvent::End),
                    UserDisconnectNotifier {
                        voice_channel_id: channel_id.clone(),
                        text_channel_id: ctx.channel_id(),
                        http: send_http,
                        cache: ctx.serenity_context().cache.clone(),
                    },
                )
            }
            Err(why) => {
                println!("Failed to join voice channel: {:?}", why);
                return Err("Failed to join voice channel".to_string().into());
            }
        }
    } else {
        return Err("You are not in a voice channel".to_string().into());
    }
    Ok(())
}
