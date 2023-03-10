package tage.nodeControllers;

import tage.*;
import org.joml.*;
import java.util.Random;

public class GlowController extends NodeController
{
    private Engine engine;
    private Random rand;

    public GlowController(Engine e)
    { 
        super();
        engine = e;
        rand = new Random();
    }

    public void apply(GameObject go)
    { 
        switch (rand.nextInt(6))
        {
            case 0:
                (go.getRenderStates()).setColor(new Vector3f(1f,0f,0f));
            case 1:
                (go.getRenderStates()).setColor(new Vector3f(0f,1f,0f));
            case 2:
                (go.getRenderStates()).setColor(new Vector3f(0f,0f,1f));
            case 3:
                (go.getRenderStates()).setColor(new Vector3f(1f,1f,0f));
            case 4:
                (go.getRenderStates()).setColor(new Vector3f(1f,0f,1f));
            case 5:
                (go.getRenderStates()).setColor(new Vector3f(0f,1f,1f));
        }
    }
}
