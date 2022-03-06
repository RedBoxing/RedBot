package baritone.bot.spec;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.TickableSoundInstance;

public class BotSoundManager extends SoundManager {
    public static final BotSoundManager INSTANCE = new BotSoundManager();

    public BotSoundManager() {
        super(null, null);
    }

    @Override
    public void play(SoundInstance sound) {

    }

    @Override
    public void playNextTick(TickableSoundInstance sound) {
        super.playNextTick(sound);
    }

    @Override
    public void play(SoundInstance sound, int delay) {
        super.play(sound, delay);
    }
}
