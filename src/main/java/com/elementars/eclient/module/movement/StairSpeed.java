package com.elementars.eclient.module.movement;


import com.elementars.eclient.module.movement;


public class StairSpeed
        extends Module {
    public StairSpeed() {
        super("StairSpeed", "Great module", Module.Category.MOVEMENT, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (StairSpeed.mc.player.onGround && StairSpeed.mc.player.posY - Math.floor(StairSpeed.mc.player.posY) > 0.0 && StairSpeed.mc.player.moveForward != 0.0f) {
            StairSpeed.mc.player.jump();
        }
    }
}

