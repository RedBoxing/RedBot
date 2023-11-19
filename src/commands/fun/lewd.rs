use poise::serenity_prelude as serenity;
use serenity::Colour;

use crate::types::{Context, Error};

/// You are really pervy...
#[poise::command(slash_command, prefix_command, nsfw_only)]
pub async fn lewd(ctx: Context<'_>) -> Result<(), Error> {
    if ctx.guild().is_some()
        && !ctx
            .guild()
            .unwrap()
            .channels
            .get(&ctx.channel_id())
            .unwrap()
            .is_nsfw()
    {
        return Err("This command can only be used in NSFW channels"
            .to_string()
            .into());
    }

    let url = reqwest::get("https://nekonya.classydev.fr/api/v1/random/lewd")
        .await?
        .json::<serde_json::Value>()
        .await?["url"]
        .as_str()
        .unwrap()
        .to_string();

    ctx.send(|m| {
        m.embed(|e| {
            e.author(|a| {
                a.name("Lewd <3").icon_url(
                    ctx.serenity_context()
                        .cache
                        .current_user()
                        .avatar_url()
                        .unwrap_or_default(),
                )
            })
            .image(url)
            .color(Colour::MAGENTA)
            .footer(|f| {
                f.text("Image by Nekonya")
                    .icon_url("https://nekonya.classydev.fr/static/assets/logo-rounded.ico")
            })
        })
    })
    .await?;

    Ok(())
}
