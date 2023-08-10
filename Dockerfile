FROM rust:1.67-alpine

WORKDIR /app

COPY . .

RUN apk add alpine-sdk yt-dlp ffmpeg

RUN cargo build --release

CMD ["./target/release/redbot"]