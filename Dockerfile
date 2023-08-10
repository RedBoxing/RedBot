FROM rust:1.67 as build

WORKDIR /app

ADD . . 

RUN apk add alpine-sdk yt-dlp ffmpeg

CMD ["cargo", "run"] 

FROM rust:1.67 as production

WORKDIR /app

COPY --from=build /app ./

RUN cargo build --release

CMD ["./target/release/redbot"]