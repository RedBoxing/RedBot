pub struct Data {
    pub track_channels: std::collections::HashMap<u64, u64>,
}
pub type Error = Box<dyn std::error::Error + Send + Sync>;
pub type Context<'a> = poise::Context<'a, Data, Error>;
