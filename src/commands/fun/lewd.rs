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

    let url = reqwest::get("https://nekos.life/api/v2/img/lewd")
        .await?
        .json::<serde_json::Value>()
        .await?["url"]
        .as_str()
        .unwrap()
        .to_string();

    ctx.send(|m| {
        m.embed(|e| {
            e.author(|a| {
                a.name("You pervert!").icon_url(
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
                println!("{:?}", option_env!("AUTHOR_ID"));
                if let Some(user) = ctx.serenity_context().cache.user(
                    option_env!("AUTHOR_ID")
                        .or(Some("0"))
                        .unwrap()
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

    Ok(())
}
