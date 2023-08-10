use std::sync::Arc;

use poise::{
    async_trait,
    serenity_prelude::{self as serenity, Cache, ChannelId},
};

use songbird::{Event, EventContext, EventHandler as VoiceEventHandler};

use serenity::http::Http;

pub struct UserDisconnectNotifier {
    pub voice_channel_id: ChannelId,
    pub text_channel_id: ChannelId,
    pub http: Arc<Http>,
    pub cache: Arc<Cache>,
}

#[async_trait]
impl VoiceEventHandler for UserDisconnectNotifier {
    async fn act(&self, ctx: &EventContext<'_>) -> Option<Event> {
        if let EventContext::ClientDisconnect(client) = ctx {
            let guild_channel = self
                .voice_channel_id
                .to_channel(&self.http)
                .await
                .unwrap()
                .guild()
                .unwrap();

            if guild_channel
                .members(&self.cache)
                .await
                .unwrap_or_default()
                .len()
                <= 1
            {
                self.text_channel_id
                    .say(
                        &self.http,
                        format!(
                            "RedBot left the voice channel because {} left the server",
                            self.cache.user(client.user_id.0).unwrap().name
                        ),
                    )
                    .await
                    .unwrap();
            }
        }

        None
    }
}
