package tage;

import org.joml.Vector3f;

import net.java.games.input.Event;
import tage.input.InputManager;
import tage.input.action.AbstractInputAction;

public class CameraOrbit3D{
    private Engine engine;
    private Camera camera;
    private GameObject avatar;
    private float cameraAzimuth, cameraElevation, cameraRadius;

    public CameraOrbit3D(Camera cam, GameObject av, String gpName, Engine e)
    {
        engine = e;
        camera = cam;
        avatar = av;
        cameraAzimuth = 0f;
        cameraElevation = 20f;
        cameraRadius = 2f;
        setupInputs(gpName);
        updateCameraPosition();
    }

    private void setupInputs(String gp)
    {
        OrbitAzimuthAction azmAction = new OrbitAzimuthAction();
        OrbitRadiusAction orbAction = new OrbitRadiusAction();
        OrbitElevationAction eleAction = new OrbitElevationAction();

        InputManager im = engine.getInputManager();

        if (gp != null)
        {
            im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Axis.RX,
                azmAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        }
        im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.J, azmAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.K, orbAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.I, eleAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

    }

    public void updateCameraPosition()
    {
        Vector3f avatarRot = avatar.getWorldForwardVector();
        double avatarAngle = Math.toDegrees((double) avatarRot.angleSigned(new Vector3f(0,0,-1), new Vector3f(0,1,0)));
        float totalAz = cameraAzimuth - (float) avatarAngle;
        double theta = Math.toRadians(totalAz);
        double phi = Math.toRadians(cameraElevation);
        float x = cameraRadius * (float)(Math.cos(phi) * Math.sin(theta));
        float y = cameraRadius * (float)(Math.sin(phi));
        float z = cameraRadius * (float)(Math.cos(phi) * Math.cos(theta));
        camera.setLocation(new Vector3f(x,y+1,z).add(avatar.getWorldLocation()));
        camera.lookAt(avatar.getWorldLocation());
    }
    private class OrbitAzimuthAction extends AbstractInputAction {
        
        public void performAction(float time, Event event)
        {
            float rotAmount;
            if (event.getValue() < -.2)
            {
                rotAmount=-2f;
            }
            else
            {
                if (event.getValue() > .2)
                    rotAmount=2f;
                else
                    rotAmount=0f;
            }
            cameraAzimuth += rotAmount;
            cameraAzimuth = cameraAzimuth % 360;
            updateCameraPosition();
        }
    }
    private class OrbitRadiusAction extends AbstractInputAction {
        
        public void performAction(float time, Event event)
        {
            float rotAmount;
            if (event.getValue() < -.2)
            {
                rotAmount=-.1f;
            }
            else
            {
                if (event.getValue() > .2)
                    rotAmount=.1f;
                else
                    rotAmount=0f;
            }
            cameraRadius += rotAmount;
            cameraRadius = cameraRadius % 360;
            updateCameraPosition();
        }
    }
    private class OrbitElevationAction extends AbstractInputAction {
        
        public void performAction(float time, Event event)
        {
            float rotAmount;
            if (event.getValue() < -.2)
            {
                rotAmount=-2f;
            }
            else
            {
                if (event.getValue() > .2)
                    rotAmount=2f;
                else
                    rotAmount=0f;
            }
            cameraElevation += rotAmount;
            cameraElevation = cameraElevation % 360;
            updateCameraPosition();
        }
    }
}

