use poise::serenity_prelude as serenity;
use serenity::Colour;

use crate::types::{Context, Error};

/// View the current queue
#[poise::command(slash_command, prefix_command, track_edits, guild_only)]
pub async fn queue(ctx: Context<'_>) -> Result<(), Error> {
    let guild = ctx
        .guild()
        .ok_or_else(|| "This command can only be used in a guild".to_string())?;

    let manager = songbird::get(ctx.serenity_context())
        .await
        .ok_or_else(|| "Songbird client not initialized".to_string())?;

    if let Some(handler_lock) = manager.get(guild.id) {
        let handler = handler_lock.lock().await;
        let queue = handler.queue().current_queue();

        ctx.send(|m| {
            m.embed(|e| {
                e.author(|a| {
                    a.name(format!(
                        "There are currently {} song in the queue",
                        queue.len()
                    ))
                    .icon_url(
                        ctx.serenity_context()
                            .cache
                            .current_user()
                            .avatar_url()
                            .unwrap_or_default(),
                    )
                })
                .description(format!(
                    "```{}```",
                    queue
                        .iter()
                        .enumerate()
                        .map(|(i, t)| format!(
                            "{}. {}",
                            i + 1,
                            t.metadata()
                                .title
                                .as_ref()
                                .unwrap_or(&"Unknown".to_string())
                        ))
                        .collect::<Vec<String>>()
                        .join("\n")
                ))
                .color(Colour::BLITZ_BLUE)
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
    } else {
        return Err("RedBot is not connected to a voice channel"
            .to_string()
            .into());
    }

    Ok(())
}
