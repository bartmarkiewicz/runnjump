package com.mygdx.runnjump.screens;

import com.mygdx.runnjump.Runnjump;

abstract class ScreenBase {

    public Runnjump theGame;

    public ScreenBase(Runnjump theGameO) {
        this.theGame = theGameO;
    }
}
