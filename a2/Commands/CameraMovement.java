package a2.Commands;

import tage.Camera;
import tage.GameObject;
import tage.input.action.AbstractInputAction;

import org.joml.*;

import a2.MyGame;
import net.java.games.input.Event;

public class CameraMovement extends AbstractInputAction
{
    private MyGame game;
    private GameObject av;
    private String dir;

    public CameraMovement (MyGame g, String d) { game = g; dir = d; }

    @Override
    public void performAction(float time, Event e)
    {
        Camera c = (game.getEngine().getRenderSystem()).getViewport("RIGHT").getCamera();
            //if (forward)
                
            c.straightMovement(game.getFrameTime()*.002f);
            
    }
}
