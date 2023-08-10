use poise::serenity_prelude as serenity;
use serenity::Colour;

use crate::types::{Context, Error};

/// Pat someone
#[poise::command(slash_command, prefix_command)]
pub async fn pat(
    ctx: Context<'_>,
    #[description = "user to pat"] user: serenity::User,
) -> Result<(), Error> {
    let url = reqwest::get("https://nekos.life/api/v2/img/pat")
        .await?
        .json::<serde_json::Value>()
        .await?["url"]
        .as_str()
        .unwrap()
        .to_string();

    ctx.send(|m| {
        m.embed(|e| {
            e.author(|a| {
                a.name(format!("{} Patted {}", ctx.author().name, user.name))
                    .icon_url(
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
