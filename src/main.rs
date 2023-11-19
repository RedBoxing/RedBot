use dotenvy::dotenv;
use poise::serenity_prelude::{self as serenity, EventHandler};
use poise::{async_trait, PrefixFrameworkOptions};

use songbird::SerenityInit;

use crate::commands::fun::hug::hug;
use crate::commands::fun::kiss::kiss;
use crate::commands::fun::lewd::lewd;
use crate::commands::fun::neko::neko;
use crate::commands::fun::pat::pat;
use crate::commands::fun::slap::slap;

use crate::commands::infos::infos::infos;

use crate::commands::music::join::join;
use crate::commands::music::play::play;
use crate::commands::music::queue::queue;
use crate::commands::music::skip::skip;
use crate::commands::music::stop::stop;

use crate::types::Data;

pub mod commands;
pub mod types;
pub mod voice;

struct Handler;

#[async_trait]
impl EventHandler for Handler {
    async fn ready(&self, ctx: serenity::Context, ready: serenity::Ready) {
        println!("{} is connected!", ready.user.name);
        ctx.set_activity(serenity::Activity::listening("https://redboxing.fr"))
            .await;
    }
}

#[tokio::main]
async fn main() {
    if cfg!(debug_assertions) {
        dotenv().expect(".env file not found");
    }

    //let http = Http::new(&std::env::var("DISCORD_TOKEN").expect("missing DISCORD_TOKEN"));

    // let bot_id = match http.get_current_application_info().await {
    //     Ok(info) => info.id,
    //     Err(why) => panic!("Could not access application info: {:?}", why),
    // };

    let framework = poise::Framework::builder()
        .options(poise::FrameworkOptions {
            prefix_options: PrefixFrameworkOptions {
                prefix: Some("~".to_string()),
                ..Default::default()
            },
            commands: vec![
                infos(),
                hug(),
                slap(),
                kiss(),
                pat(),
                neko(),
                lewd(),
                join(),
                play(),
                skip(),
                stop(),
                queue(),
            ],
            ..Default::default()
        })
        .token(std::env::var("DISCORD_TOKEN").expect("missing DISCORD_TOKEN"))
        .intents(serenity::GatewayIntents::non_privileged())
        .client_settings(|client_builder| client_builder.event_handler(Handler).register_songbird())
        .setup(|ctx, _ready, framework| {
            Box::pin(async move {
                poise::builtins::register_globally(ctx, &framework.options().commands).await?;
                Ok(Data {
                    track_channels: std::collections::HashMap::new(),
                })
            })
        });

    framework.run().await.unwrap();
}
