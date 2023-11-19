use poise::serenity_prelude as serenity;
use serenity::Colour;

use crate::types::{Context, Error};

/// Hug someone
#[poise::command(slash_command, prefix_command)]
pub async fn hug(
    ctx: Context<'_>,
    #[description = "user to hug"] user: serenity::User,
) -> Result<(), Error> {
    let url = reqwest::get("https://nekonya.classydev.fr/api/v1/random/hug")
        .await?
        .json::<serde_json::Value>()
        .await?["url"]
        .as_str()
        .unwrap()
        .to_string();

    ctx.send(|m| {
        m.embed(|e| {
            e.author(|a| {
                a.name(format!("{} Hugged {}", ctx.author().name, user.name))
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
                f.text("Image by Nekonya")
                    .icon_url("https://nekonya.classydev.fr/static/assets/logo-rounded.ico")
            })
        })
    })
    .await?;

    Ok(())
}
