use poise::serenity_prelude as serenity;
use serenity::Colour;

use std::{fs::File, io::BufRead};
use sysinfo::{System, SystemExt};

use crate::types::{Context, Error};

/// Give informations about the bot
#[poise::command(slash_command, prefix_command)]
pub async fn infos(ctx: Context<'_>) -> Result<(), Error> {
    let cpufile = File::open("/proc/cpuinfo").unwrap();
    let bufreader = std::io::BufReader::new(cpufile);

    let mut cpu = String::new();

    for line in bufreader.lines() {
        let line = line.unwrap();
        if line.starts_with("model name") {
            cpu = line.split(": ").collect::<Vec<&str>>()[1].to_string();
            break;
        }
    }

    let mut system = System::new_all();
    system.refresh_memory();

    ctx.send(|m| {
        m.embed(|e| {
            e.title("RedBot - Informations");
            e.field(
                "Versions",
                format!(
                    "Serenity: `{}`\nRedBot: `{}`",
                    "0.11.6",
                    option_env!("CARGO_PKG_VERSION").or(Some("UNK")).unwrap()
                ),
                true,
            );
            e.field(
                "Git",
                format!(
                    "branch: `{}`\ncommit: `{}`",
                    option_env!("GIT_BRANCH").unwrap_or("UNK"),
                    option_env!("GIT_COMMIT").unwrap_or("UNK")
                ),
                true,
            );
            e.field(
                "Stats",
                format!(
                    "Users: `{}`\nGuilds: `{}`",
                    ctx.serenity_context().cache.user_count().to_string(),
                    ctx.serenity_context().cache.guild_count().to_string()
                ),
                true,
            );
            e.field(
                "Server",
                format!(
                    "CPU: `{}`\nMemory: `{}`\nPod: `{}`",
                    cpu,
                    format!(
                        "{:.2} GB / {:.2} GB",
                        (system.used_memory() as f32) / 1024f32 / 1024f32 / 1024f32,
                        (system.total_memory() as f32) / 1024f32 / 1024f32 / 1024f32
                    ),
                    gethostname::gethostname().into_string().unwrap_or_default()
                ),
                false,
            );
            e.thumbnail(
                ctx.serenity_context()
                    .cache
                    .current_user()
                    .avatar_url()
                    .unwrap_or_default(),
            );
            e.footer(|f| {
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
            });
            e.color(Colour::BLITZ_BLUE);
            e
        })
    })
    .await?;
    Ok(())
}
