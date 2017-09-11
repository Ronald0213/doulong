package com.hotyi.hotyi.other.hotyiClass;

/**
 * Created by HOTYI on 2017/9/11.
 */

public class GameInfo {

    String gameId;
    String gameName;
    String gameLogo;

    public GameInfo(String gameId, String gameName, String gameLogo) {
        this.gameId = gameId;
        this.gameName = gameName;
        this.gameLogo = gameLogo;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGameLogo() {
        return gameLogo;
    }

    public void setGameLogo(String gameLogo) {
        this.gameLogo = gameLogo;
    }
}

