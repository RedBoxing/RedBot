use poise::serenity_prelude as serenity;
use serenity::Colour;

use crate::types::{Context, Error};

/// encode a base64 string
#[poise::command(slash_command, prefix_command, nsfw_only)]
pub async fn base64(ctx: Context<'_>, text: String) -> Result<(), Error> {
    let encoded = base64::encode(text);
    
    ctx

    Ok(())
}
