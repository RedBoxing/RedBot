use poise::serenity_prelude as serenity;
use serenity::Colour;

use crate::types::{Context, Error};

/// Chat with RedBot
#[poise::command(slash_command, prefix_command)]
pub async fn hug(ctx: Context<'_>, message: String) -> Result<(), Error> {
    let server_url = std::env::var("CHAT_SERVER_URL").expect("Chat server is not available");
    let url = format!("{}/chat", server_url);

    let client = reqwest::Client::new();

    let res = client
        .post(&url)
        .json(&serde_json::json!({ "text": message }))
        .send()
        .await?;

    let res = res.json::<serde_json::Value>().await?;
    let response = res["response"]
        .as_str()
        .unwrap_or_else(|| "RedBot is not available");

    ctx.say(response).await?;
    Ok(())
}
