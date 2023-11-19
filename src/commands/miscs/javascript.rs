use js_sandbox::{AnyError, JsValue, Script};
use poise::serenity_prelude as serenity;
use serenity::Colour;

use crate::types::{Context, Error};

fn my_eval(code: &str) -> Result<JsValue, AnyError> {
    let code = format!(
        "
		function __rust_expr() {{
			{}
		}}
	",
        if !code.split('\n').any(|l| l.starts_with("return")) {
            format!("return {}", code)
        } else {
            code.to_string()
        }
    );

    let script = Script::from_string(&code);
    if let Err(err) = script {
        return Err(AnyError::from(err));
    }

    let mut script = script.unwrap();
    script.call("__rust_expr", ())
}

/// Execute javascript code in a isolated environment
#[poise::command(slash_command, prefix_command)]
pub async fn javascript(ctx: Context<'_>, code: String) -> Result<(), Error> {
    let code = if code.starts_with("```") {
        code.split('\n')
            .skip(1)
            .take_while(|l| !l.starts_with("```"))
            .collect::<Vec<_>>()
            .join("\n")
    } else {
        code
    };

    let result = my_eval(&code);

    if let Err(err) = result {
        return Err(format!("```javascript\n{}```\n> {}", code, err).into());
    }

    let result = result.unwrap();
    ctx.send(|m| {
        m.embed(|e| {
            e.author(|a| {
                a.name("Javascript Runtime").icon_url(
                    ctx.serenity_context()
                        .cache
                        .current_user()
                        .avatar_url()
                        .unwrap_or_default(),
                )
            })
            .description(format!(
                "```javascript\n{}\n```\n> {}",
                code,
                result.to_string()
            ))
            .color(Colour::FOOYOO)
            .footer(|f| {
                if let Some(user) = ctx.serenity_context().cache.user(
                    std::env::var("AUTHOR_ID")
                        .expect("missing AUTHOR_ID")
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
