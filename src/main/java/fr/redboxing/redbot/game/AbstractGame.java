package fr.redboxing.redbot.game;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGame<T extends GameTurn> {
    @Getter
    @Setter
    private GameState state = GameState.OVER;
    @Getter
    private final List<User> players = new ArrayList<>();
    @Getter
    @Setter
    private String activePlayerId = "";
    @Getter
    @Setter
    private String winnerPlayerId = "";
    @Getter
    @Setter
    private long lastTurnTimestamp = System.currentTimeMillis();


    public abstract String getName();
    public abstract int getTotalPlayers();
    public abstract boolean isValidMove(User player, T turnInfo);
    protected abstract void doPlayerMove(User player, T turnInfo);
    protected abstract boolean isGameOver();

    public T getGameTurnInstance() {
        Class<?> turnTypeClass = (Class<?>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        try {
            return (T) turnTypeClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean playTurn(User player, T turnInfo) {
        if(!this.state.equals(GameState.PLAYING) && !this.state.equals(GameState.READY)) return false;
        if(!this.isPlayerTurn(player)) return false;
        doPlayerMove(player, turnInfo);
        if(isGameOver()) {
            this.state = GameState.OVER;
        }

        this.endTurn();
        this.lastTurnTimestamp = System.currentTimeMillis();
        return true;
    }

    public boolean addPlayer(User player) {
        if(!this.state.equals(GameState.INITIALIZING)) return false;
        if(this.players.contains(player)) return false;
        if(this.players.size() == this.getTotalPlayers()) return false;
        this.players.add(player);
        if(this.players.size() == this.getTotalPlayers()) {
            this.activePlayerId = this.players.get(0).getId();
            this.state = GameState.READY;
        }
        return true;
    }

    public User getPlayerById(String id) {
        for(User player : this.players) {
            if(player.getId().equals(id)) return player;
        }

        return null;
    }

    public void endTurn() {
        this.activePlayerId = this.players.get(this.players.indexOf(this.getPlayerById(this.activePlayerId)) + 1).getId();
    }

    public boolean isPlayerTurn(User player) {
        return this.activePlayerId.equals(player.getId());
    }

    public void reset() {
        this.state = GameState.INITIALIZING;
        this.players.clear();
        this.activePlayerId = "";
        this.winnerPlayerId = "";
    }

    public boolean waitingForPlayer() {
        return this.state.equals(GameState.INITIALIZING);
    }

    public User getPlayer(int index) {
        return this.players.get(index);
    }

    public User getActivePlayer() {
        return this.getPlayerById(this.activePlayerId);
    }
}
