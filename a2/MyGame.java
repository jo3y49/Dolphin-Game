package a2;

import tage.*;
import tage.shapes.*;
import tage.input.*;
import tage.input.action.*;
import net.java.games.input.Component.Identifier.*;
import java.lang.Math;
import java.util.ArrayList;
import org.joml.*;
import a2.Commands.*;
import a2.Shapes.*;

public class MyGame extends VariableFrameRateGame
{
	private static Engine engine;
	private int collectedPrizes = 0;
	private float dolphinExhaustion = -11;
	private double lastFrameTime, currFrameTime;
	private double elapsTime;
	private InputManager im;
	private CameraOrbit3D orbitController;
	private GameObject avatar, cub, tor, pyr, sph, x, y, z;
	private ObjShape dolS, cubS, torS, pyrS, sphS, linxS, linyS, linzS;
	private TextureImage doltx, brick, earth, cubePattern;
	private Light light1;
	private ArrayList<GameObject> prizes = new ArrayList<>();

	public MyGame() { super(); }

	public static void main(String[] args)
	{	MyGame game = new MyGame();
		engine = new Engine(game);
		game.initializeSystem();
		game.game_loop();
	}

	@Override
	public void loadShapes()
	{	dolS = new ImportedModel("dolphinHighPoly.obj");
		cubS = new Cube();
		torS = new Torus(.5f, .2f, 48);
		pyrS = new ManualPyramid();
		sphS = new Sphere();
		linxS = new Line(new Vector3f(0f, 0f, 0f), new Vector3f(5f, 0f, 0f));
		linyS = new Line(new Vector3f(0f, 0f, 0f), new Vector3f(0f, 5f, 0f));
		linzS = new Line(new Vector3f(0f, 0f, 0f), new Vector3f(0f, 0f, -5f));
	}

	@Override
	public void loadTextures()
	{	
		doltx = new TextureImage("Dolphin_HighPolyUV.png");
		cubePattern = new TextureImage("Cube_Decoration.png");
	}	 

	@Override
	public void buildObjects()
	{	Matrix4f initialTranslation, initialScale, initialRotation;

		// build dolphin in the center of the window
		avatar = new GameObject(GameObject.root(), dolS, doltx);
		initialTranslation = (new Matrix4f()).translation(-1,0,1);
		initialScale = (new Matrix4f()).scaling(3.0f);
		initialRotation = (new Matrix4f()).rotationY((float)java.lang.Math.toRadians(135f));
		avatar.setLocalTranslation(initialTranslation);
		avatar.setLocalScale(initialScale);
		avatar.setLocalRotation(initialRotation);

		cub = new GameObject(GameObject.root(), cubS, cubePattern);
		initialTranslation = (new Matrix4f()).translation(20,10,-10);
		initialScale = (new Matrix4f()).scaling(.6f);
		cub.setLocalTranslation(initialTranslation);
		cub.setLocalScale(initialScale);
		prizes.add(cub);

		sph = new GameObject(GameObject.root(), sphS, earth);
		initialTranslation = (new Matrix4f()).translation(-25,0,-5);
		initialScale = (new Matrix4f()).scaling(.7f);
		sph.setLocalTranslation(initialTranslation);
		sph.setLocalScale(initialScale);
		prizes.add(sph);

		tor = new GameObject(GameObject.root(), torS, earth);
		initialTranslation = (new Matrix4f()).translation(11, -20, 10);
		tor.setLocalTranslation(initialTranslation);
		initialScale = (new Matrix4f()).scaling(1f);
		tor.setLocalScale(initialScale);
		prizes.add(tor);

		// add X, Y, -Z axes
		x = new GameObject(GameObject.root(), linxS);
		y = new GameObject(GameObject.root(), linyS);
		z = new GameObject(GameObject.root(), linzS);
		(x.getRenderStates()).setColor(new Vector3f(1f,1f,0f));
		(y.getRenderStates()).setColor(new Vector3f(0f,1f,0f));
		(z.getRenderStates()).setColor(new Vector3f(0f,0f,1f));

		//build pyramid
		pyr = new GameObject(GameObject.root(), pyrS, brick);
		initialTranslation = (new Matrix4f()).translation(0,0,0);
		pyr.setLocalTranslation(initialRotation);
		pyr.getRenderStates().hasLighting(true);
	}

	@Override
	public void initializeLights()
	{	Light.setGlobalAmbient(0.5f, 0.5f, 0.5f);
		light1 = new Light();
		light1.setLocation(new Vector3f(5.0f, 4.0f, 2.0f));
		(engine.getSceneGraph()).addLight(light1);
	}

	@Override
	public void createViewports()
	{
		(engine.getRenderSystem()).addViewport("LEFT", 0, 0, 1f, 1f);
		(engine.getRenderSystem()).addViewport("RIGHT", .75f, 0, .25f, .25f);

		Viewport leftVP = (engine.getRenderSystem()).getViewport("LEFT");
		Viewport rightVP = (engine.getRenderSystem()).getViewport("RIGHT");
		Camera leftCamera = leftVP.getCamera();
		Camera rightCamera = rightVP.getCamera();

		rightVP.setHasBorder(true);
		rightVP.setBorderWidth(4);
		rightVP.setBorderColor(0, 1, 0);

		leftCamera.setLocation(new Vector3f(-2,0,2));
		leftCamera.setU(new Vector3f(1,0,0));
		leftCamera.setV(new Vector3f(0,1,0));
		leftCamera.setN(new Vector3f(0,0,1));

		rightCamera.setLocation(new Vector3f(0,2,0));
		rightCamera.setU(new Vector3f(1,0,0));
		rightCamera.setV(new Vector3f(0,0,-1));
		rightCamera.setN(new Vector3f(0,-1,0));
	}
	
	@Override
	public void initializeGame()
	{	lastFrameTime = System.currentTimeMillis();
		currFrameTime = System.currentTimeMillis();
		elapsTime = 0.0;
		(engine.getRenderSystem()).setWindowDimensions(1900,1000);

		im = engine.getInputManager();

		String gpName = im.getFirstGamepadName();
		Camera c = (engine.getRenderSystem()).getViewport("LEFT").getCamera();
		orbitController = new CameraOrbit3D(c, avatar, gpName, engine);

		StraightMovementController moveController = new StraightMovementController(this);
		StraightMovement moveForward = new StraightMovement(this, true);
		StraightMovement moveBackward = new StraightMovement(this, false);

		YawController YawController = new YawController(this);
		Yaw yawLeft = new Yaw(this, true);
		Yaw yawRight = new Yaw(this, false);

		PitchController pitchController = new PitchController(this);
		Pitch pitchUp = new Pitch(this, true);
		Pitch pitchDown = new Pitch(this, false);

		setHeldButtonToGamepad(Axis.Y, moveController);
		setHeldButtonToGamepad(Axis.X, YawController);
		setHeldButtonToGamepad(Axis.RY, pitchController);

		setHeldActionToKeyboard(Key.W, moveForward);
		setHeldActionToKeyboard(Key.S, moveBackward);
		setHeldActionToKeyboard(Key.A, yawLeft);
		setHeldActionToKeyboard(Key.D, yawRight);
		setHeldActionToKeyboard(Key.UP, pitchUp);
		setHeldActionToKeyboard(Key.DOWN, pitchDown);
	}
	
	@Override
	public void update()
	{	
		lastFrameTime = currFrameTime;
		currFrameTime = System.currentTimeMillis();
		elapsTime += (currFrameTime - lastFrameTime) / 1000.0;

		// build and set HUD
		String dispStr1;
		if (dolphinExhaustion < 100)
		{
			int dolphinExhaustInt = Math.round(dolphinExhaustion);
			String dolphinExhaustStr = Integer.toString(dolphinExhaustInt);
			dispStr1 = "Dolphin Exhaustion = " + dolphinExhaustStr + "%";
		} else
		{
			dispStr1 = "Your dolphin is dead!";
			if (avatar.getRenderStates().renderingEnabled())
			{
				avatar.getRenderStates().disableRendering();
			}
		}
		String collectedStr = Integer.toString(collectedPrizes);
		String dispStr2 = "Collected Prizes = " + collectedStr;

		Vector3f hud1Color = new Vector3f(1,0,0);
		Vector3f hud2Color = new Vector3f(0,0,1);
		(engine.getHUDmanager()).setHUD1(dispStr1, hud1Color, 15, 15);
		(engine.getHUDmanager()).setHUD2(dispStr2, hud2Color, 500, 15);

		// update inputs and camera
		im.update((float)elapsTime);

		orbitController.updateCameraPosition();
		if (dolphinExhaustion < 100)
			//dolphinExhaustion += (currFrameTime - lastFrameTime) / 200f;

		checkPrizeCollision();
		if (dolphinExhaustion > 0 && dolphinExhaustion < 100 && avatar.getLocalLocation().distance(pyr.getLocalLocation()) <= 2f)
			dolphinExhaustion -= (currFrameTime - lastFrameTime) / 50f;
	}

	private void checkPrizeCollision()
	{
		Camera cam = (engine.getRenderSystem().getViewport("MAIN").getCamera());

		for (int i = 0; i < prizes.size(); i++)
		{
			if (cam.getLocation().distance(prizes.get(i).getLocalLocation()) <= .75f)
			{
				prizes.get(i).getRenderStates().disableRendering();
				collectedPrizes++;
				prizes.remove(i);
			}
		}
	}

	private void setHeldActionToKeyboard(net.java.games.input.Component.Identifier.Key key, IAction action)
	{
		im.associateActionWithAllKeyboards(
			key, action, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	}
	private void setPressedActionToKeyboard(net.java.games.input.Component.Identifier.Key key, IAction action)
	{
		im.associateActionWithAllKeyboards(
			key, action, InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
	}
	private void setHeldButtonToGamepad(net.java.games.input.Component.Identifier button, IAction action)
	{
		im.associateActionWithAllGamepads(
			button, action, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	}
	private void setPressedButtonToGamepad(net.java.games.input.Component.Identifier button, IAction action)
	{
		im.associateActionWithAllGamepads(
			button, action, InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
	}

	public GameObject getAvatar() { return avatar; }

	public float getFrameTime() { return (float)(currFrameTime - lastFrameTime); }
	
}