package mygame;

import animations.AIAnimationControl;
import characters.AICharacterControl;
import characters.MyGameCharacterControl;
import characters.NavMeshNavigationControl;
import characters.TargetControl;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import physics.PhysicsTestHelper;

/**
 * Spank the monkey before he breaks your balls!
 * @author dustinoakes
 */
public class Main extends SimpleApplication implements PhysicsCollisionListener {

    BulletAppState bulletAppState;
    private Vector3f normalGravity = new Vector3f(0, -9.81f, 0);
    
    private Boolean gameOver = false;
    
    public static Material lineMat; 
    
    //Player initionals
    private String playerInitials = "DKO";
    
    //high score
    private int highScore = 0;
    
    //game score
    private int gameScore = 0;
    
    //sphere definition for orbs
    private static Sphere sphere = new Sphere(16, 16, 1f);
    
    //RigidBodyControl for orbs
    private RigidBodyControl orbRBC;
    private RigidBodyControl bulletRBC;
    
    //spatials for orbs
    private Spatial orb1;
    private Spatial orb2;
    private Spatial orb3;
    private Spatial orb4;
    
    //controls for orbs
    TargetControl orb1Control;
    TargetControl orb2Control;
    TargetControl orb3Control;
    TargetControl orb4Control;
    
    private Node aiCharacter;
    List<Spatial> targets = new ArrayList<>();
    
    
    //object names
    private final String JAIME = "JaimeGeom-ogremesh";
    private final String ORB_ONE = "orb1";
    private final String ORB_TWO = "orb2";
    private final String ORB_THREE = "orb3";
    private final String ORB_FOUR = "orb4";
    private final String BULLET = "bullet";
    private final String SHOOT = "shoot";
    
    //constants for point values
    private final int ONE_POINTS = 10;
    private final int TWO_POINTS = 20;
    private final int THREE_POINTS = 30;
    private final int FOUR_POINTS = 40;
    private final int JAMIE_POINTS = 5;
    private final int BONUS_POINTS = 1000;
    
    //counts the number of iterations
    private int updateCount = 0;
    
    //store points for each of the targets 
    private int monkeySpanks = 0;
    private int onePoints = 0;
    private int twoPoints = 0;
    private int threePoints = 0;
    private int fourPoints = 0;
    private int bulletCount = 0;
    private int bonusPoints = 0;
    
    //number of iterations between each gui and output file update
    private final int OUTPUT_SPEED = 60;
    
    //timer based on output speed
    private final int TIME_LIMIT = OUTPUT_SPEED * 5;
    private int countdown = TIME_LIMIT;
   
    
    /**
     * has AI character collided with orbs?
     * updated in collision listener
     */
    private Boolean caughtOrb1 = false;
    private Boolean caughtOrb2 = false;
    private Boolean caughtOrb3 = false;
    private Boolean caughtOrb4 = false;
    
    //gui text
    private BitmapText crosshairs;
    private BitmapText statusText;
    private BitmapText positionsText;
    private BitmapText pointsText;
    private BitmapText hitsText;
    private BitmapText directionsText;
    private BitmapText countdownText;
    
    private String DIRECTIONS = "Move using WASD keys."
            + "\nLook using mouse."
            + "\nFire bullets using left mouse button."
            + "\nRed ball worth " + ONE_POINTS + " points."
            + "\nWhite ball worth " + TWO_POINTS + " points."
            + "\nBlue ball worth " + THREE_POINTS + " points."
            + "\nBlack ball worth " + FOUR_POINTS + " points."
            + "\nMonkey worth " + JAMIE_POINTS + " points."
            + "\nSpanking the monkey = " + BONUS_POINTS + " bonus points."
            + "\n\nGame ends when the monkey breaks all"
            + "\nof your balls, when you spank the monkey,"
            + "\nor when time runs out.";
    
    private NavMeshNavigationControl navMesh;
    private AIAnimationControl aiAnim;
    
    //constant for the crosshairs symbol
    private final String CROSSHAIRS = "+";
    
    //output file name
    private final String fileName = "OrbCoordinates.csv";
    private final String scoreFile = "scores.txt";
    private boolean scoresUpdated;
    
    //array to store coordinates of AI and 3 orbs
    private String[][] positionArray = new String[100][4];
    private String[][] scoreArray = new String[5][2];
    private String[][] newScoreArray = new String[5][2];
    
    //count number of positions written to array
    private int count = 0;
    
    MyGameCharacterControl pOneControl;
    
    AICharacterControl jamieControl;
    
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    } //end main
    
    @Override
    public void simpleInitApp() {
        
        /**
         * read high scores from file into array
         * then retrieve highest (first) score from array
         */
        scoreFileToArray();
        highScore = Integer.parseInt(scoreArray[0][1]);
        
        lineMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        
        setDisplayStatView(false);
        setDisplayFps(false);
        
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().setGravity(normalGravity);
        
        /**
         * Add InputManager action: Left click triggers shooting.
         */
        inputManager.addMapping(SHOOT, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, SHOOT);
        
        getFlyByCamera().setMoveSpeed(45f);
        cam.setLocation(new Vector3f(20, 20, 20));
        cam.lookAt(new Vector3f(0,0,0), Vector3f.UNIT_Y);
        
        Node scene = (Node) assetManager.loadModel("Scenes/CMSC325proj1Scene.j3o");
        rootNode.attachChild(scene);
        
        Spatial terrain = scene.getChild("terrain-CMSC325proj1Scene");
        terrain.addControl(new RigidBodyControl(0));
        bulletAppState.getPhysicsSpace().addAll(terrain);
        
        //add collision listener
        bulletAppState.getPhysicsSpace().addCollisionListener(this);
        
        //Create the Physics World based on the Helper class
        PhysicsTestHelper.createWorld(rootNode, assetManager, bulletAppState.getPhysicsSpace());
        
//        //debug navMesh
//        Geometry navGeom = new Geometry("NavMesh");
//        navGeom.setMesh(((Geometry)scene.getChild("NavMesh" )).getMesh());
//        Material green = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        green.setColor("Color", ColorRGBA.Green);
//        green.getAdditionalRenderState().setWireframe(true);
//        navGeom.setMaterial(green);

//        rootNode.attachChild(navGeom); 
        
        
//        Node pOneNode = (Node)assetManager.loadModel("Models/at-strig/at-strig.j3o");
//        pOneControl = new MyGameCharacterControl(0f,2.5f,8f);
//        pOneControl.setCamera(cam);
//        pOneNode.addControl(pOneControl);
//        pOneControl.setGravity(normalGravity);
//        pOneControl.setPhysicsLocation(new Vector3f(-4,0,0));
//        bulletAppState.getPhysicsSpace().add(pOneControl);
//        
//        InputAppState pOneState = new InputAppState();
//        pOneState.setCharacter(pOneControl);
//        stateManager.attach(pOneState);
//        rootNode.attachChild(pOneNode);
//       
//        Node pTwoNode = (Node)assetManager.loadModel("Models/trainingtarget/trainingtarget.j3o");
//        TargetControl pTwoControl = new TargetControl(0f,2.5f,8f);
//        pTwoNode.addControl(pTwoControl);
//        pTwoControl.setGravity(normalGravity);
//        pTwoControl.setPhysicsLocation(new Vector3f(4,0,0));
//        bulletAppState.getPhysicsSpace().add(pTwoControl);
//        
////        InputAppState pTwoState = new InputAppState();
////        pTwoState.setCharacter(pTwoControl);
////        stateManager.attach(pTwoState);
//        rootNode.attachChild(pTwoNode);
        
        // AI Character
        aiCharacter = (Node) assetManager.loadModel("Models/Jaime/Jaime.j3o");

        jamieControl = new AICharacterControl(0.3f, 2.5f, 8f);
        aiCharacter.addControl(jamieControl);
        
        aiAnim = new AIAnimationControl("animations/resources/animations-jaime.properties");
        aiCharacter.addControl(aiAnim);
        
        //aiCharacter.addControl(new AIControl());
        
        bulletAppState.getPhysicsSpace().add(jamieControl);
        aiCharacter.setLocalTranslation(0, 1, 0);
        aiCharacter.setLocalScale(2f);
        
        scene.attachChild(aiCharacter);
        
        navMesh = new NavMeshNavigationControl((Node) scene);
        aiCharacter.addControl(navMesh);
        
        //Add a custom font and text to the scene
        BitmapFont myFont = assetManager.loadFont("Interface/Fonts/BasicFont.fnt");
        
        crosshairs = new BitmapText(myFont, true);
        crosshairs.setText(CROSSHAIRS);
        crosshairs.setColor(ColorRGBA.Red);
        crosshairs.setSize(guiFont.getCharSet().getRenderedSize()*4);
        
        //Set crosshairs in the middle of the screen
        crosshairs.setLocalTranslation(settings.getWidth() /2 , settings.getHeight() / 2 + crosshairs.getLineHeight(), 0f);
        guiNode.attachChild(crosshairs);
        
        //GUI text for Jamie's progress
        statusText = new BitmapText(myFont, true);
        statusText.setText("SPANK THE MONKEY BEFORE HE BREAKS YOUR BALLS!!!");
        statusText.setColor(ColorRGBA.Red);
        statusText.setSize(guiFont.getCharSet().getRenderedSize()*2);
        
        //Set status in the upper left of the screen
        statusText.setLocalTranslation(settings.getWidth() * 0.05f, settings.getHeight() * 0.95f, 0f);
        guiNode.attachChild(statusText);
        
//        //GUI text for orb positions
//        positionsText = new BitmapText(myFont, true);
//        positionsText.setText("positions\ntext\ntest");
//        positionsText.setColor(ColorRGBA.Red);
//        positionsText.setSize(guiFont.getCharSet().getRenderedSize()*2);
        
//        //Set positions in the upper right of the screen
//        positionsText.setLocalTranslation(settings.getWidth() * 0.75f, settings.getHeight() * 0.95f, 0f);
//        guiNode.attachChild(positionsText);
        
        //GUI text for points
        pointsText = new BitmapText(myFont, true);
        pointsText.setText("");
        pointsText.setColor(ColorRGBA.Red);
        pointsText.setSize(guiFont.getCharSet().getRenderedSize()*2);
        
        //Set points in the upper right of the screen
        pointsText.setLocalTranslation(settings.getWidth() * 0.85f, settings.getHeight() * 0.95f, 0f);
        guiNode.attachChild(pointsText);
        
        //GUI text for timer
        countdownText = new BitmapText(myFont, true);
        countdownText.setText("TIME REMAINING:  " + countdown);
        countdownText.setColor(ColorRGBA.Red);
        countdownText.setSize(guiFont.getCharSet().getRenderedSize()*2);
        
        //Set timer in lower right of the screen
        countdownText.setLocalTranslation(settings.getWidth() * 0.85f, settings.getHeight() * 0.05f, 0f);
        guiNode.attachChild(countdownText);
        
        //GUI text for hit count
        hitsText = new BitmapText(myFont, true);
        hitsText.setText("0% SPANKED");
        hitsText.setColor(ColorRGBA.Red);
        hitsText.setSize(guiFont.getCharSet().getRenderedSize()*4);
        
        //Set hit count in the upper middle of the screen
        hitsText.setLocalTranslation(settings.getWidth() / 2, settings.getHeight() * 0.95f, 0f);
        guiNode.attachChild(hitsText);
        
        //GUI text for directions
        directionsText = new BitmapText(myFont, true);
        directionsText.setText(DIRECTIONS);
        directionsText.setColor(ColorRGBA.Red);
        directionsText.setSize(guiFont.getCharSet().getRenderedSize()*2);
        
        //Set directions in the lower left of the screen
        directionsText.setLocalTranslation(settings.getWidth() * 0.05f, settings.getHeight() * 0.3f, 0f);
        guiNode.attachChild(directionsText);
        
        //create ORBs
        orb1 = makeOrb(ORB_ONE, ColorRGBA.Red, bulletAppState);
        orb2 = makeOrb(ORB_TWO, ColorRGBA.White, bulletAppState);
        orb3 = makeOrb(ORB_THREE, ColorRGBA.Blue, bulletAppState);        
        orb4 = makeOrb(ORB_FOUR, ColorRGBA.Black, bulletAppState);
        
        //add ORBs to Jamie's target list
        targets.add(orb1);
        targets.add(orb2);
        targets.add(orb3);
        targets.add(orb4);
        aiAnim.setTargetList(targets);
        
    } //end simpleInitApp
    
    /**
     * geometry definition for orbs
     * @param name
     * @param loc
     * @param color
     * @return 
     */
    private Geometry orb(String name, Vector3f loc, ColorRGBA color) {
        Geometry geom = new Geometry(name, sphere);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        geom.setMaterial(mat);
        geom.setLocalTranslation(loc);
        return geom;
    }
    
    /**
     * Creates an orb and adds it to the scene
     * @param name
     * @param bulletAppState
     * @return 
     */
    private Spatial makeOrb(String name, ColorRGBA color, BulletAppState bulletAppState) {
        //randomize origin coordinates
        Vector3f loc = new Vector3f(0,30,0);
//            FastMath.nextRandomInt(-50, 50),
//            FastMath.nextRandomInt(0, 50),
//            FastMath.nextRandomInt(-50, 50));
        
        Geometry geom = orb(name, loc, color);
        
        rootNode.attachChild(geom);
        
        orbRBC = new RigidBodyControl(0.05f);
        geom.addControl(orbRBC);
        
        
        bulletAppState.getPhysicsSpace().add(orbRBC);
        
        //randomize direction and velocity of the orb
        orbRBC.setLinearVelocity(new Vector3f(
            FastMath.nextRandomInt(-50, 50),
            FastMath.nextRandomInt(0, 50),
            FastMath.nextRandomInt(-50, 50)));
        
        //set orbs friction coeficient to zero
        orbRBC.setFriction(0);
        
        return geom;
    } //end makeOrb

    /**
     * 
     * @param tpf 
     */
    @Override
    public void simpleUpdate(float tpf) {
    
        if(!gameOver){

            //counts the number of updates
            updateCount++;

            gameScore = onePoints + twoPoints + threePoints + fourPoints + monkeySpanks + bonusPoints;

            //if(gameScore > highScore){highScore = gameScore;}

            pointsText.setText("RED BALL Points:  " + onePoints +
                               "\nWHITE BALL Points:  " + twoPoints +
                               "\nBLUE BALL Points:  " + threePoints +
                               "\nBLACK BALL Points:  " + fourPoints + 
                               "\nMONKEY Points:  " + monkeySpanks + 
                               "\nTOTAL SCORE:  " + gameScore +
                               "\n\nHIGH SCORE:  " + highScore + 
                               "\n\nBULLETS Fired:  " + bulletCount);

            /**
             * check to see if the update number is a multiple of the output speed
             * only update GUI and position array every 'OUTPUT_SPEED' rounds
             */
            if (updateCount % OUTPUT_SPEED == 0){
                
                //decrement timer and update GUI text
                countdown--;
                countdownText.setText("TIME REMAINING:  " + countdown);
                
                //if timer runs out, end game
                if (countdown==0){gameOver = true;}
                
    //            //update GUI text
    //            positionsText.setText("ORB 1: " + orb1.getLocalTranslation() +
    //                                "\nORB 2: " + orb2.getLocalTranslation() +
    //                                "\nORB 3: " + orb3.getLocalTranslation());

                //add positions to array for output
                if (count < 100) {
                    positionArray[count][0] = aiCharacter.getLocalTranslation().toString().replace(",", ";");
                    positionArray[count][1] = orb1.getLocalTranslation().toString().replace(",", ";");
                    positionArray[count][2] = orb2.getLocalTranslation().toString().replace(",", ";");  
                    positionArray[count][3] = orb3.getLocalTranslation().toString().replace(",", ";");
                    count++;
                } //end if

            } //end if

            /**
             * once there are 100 rows in the array, output to file
             */
            if (count == 100){
                positionArrayToFile(positionArray, fileName);
            } //end if

            /**
            * Check if jaime has caught any orbs.
            * Update GUI and change target accordingly.
            */

            if (!monkeySpanked()) {
                if (!caughtOrb1) {
                    navMesh.moveTo(orb1.getLocalTranslation());
                } else {
                    if (!caughtOrb2) {
                        statusText.setText("HE BROKE YOUR FIRST BALL!");
                        navMesh.moveTo(orb2.getLocalTranslation());
                    } else {
                        if (!caughtOrb3) {
                            statusText.setText("HE GOT ANOTHER ONE!");
                            navMesh.moveTo(orb3.getLocalTranslation());
                        } else {    
                            if (!caughtOrb4) {
                                statusText.setText("JUST ONE MORE LEFT!!");
                                navMesh.moveTo(orb4.getLocalTranslation());
                            } else {
                                statusText.setText("ALL YOUR BALLS ARE BELONG TO MONKEY!!!");
                                navMesh.moveTo(new Vector3f(0, 0, 0));
                                gameOver = true;
                            } //end else caughtOrb4
                        } //end else caughtOrb3
                    } //end else caughtOrb2
                } //end else caughtOrb1
            } else {
                statusText.setText("YOU SPANKED THE MONKEY!!!\nHE'S GOING HOME.");
                navMesh.moveTo(new Vector3f(0, 1, 0));
                bonusPoints += BONUS_POINTS;
                gameOver = true;
            } //end if monkeySpanked

        } else {
            if(!scoresUpdated){
                updateScoreArray();
                crosshairs.setText("GAME OVER!\n" + scoreArrayToString());
                scoreArrayToFile();
                scoresUpdated = true;
            }
        } //end if gameOver
        
    } //end simpleUpdate
    
    
    /**
     * collision listener checks to see if Jamie 
     * has caught any orbs or if any targets have been shot.
     * @param event 
     */
    @Override
    public void collision(PhysicsCollisionEvent event) {
  
        if ((event.getNodeA().getName().equals(JAIME)
                && event.getNodeB().getName().equals(ORB_ONE))
                || (event.getNodeA().getName().equals(ORB_ONE)
                && event.getNodeB().getName().equals(JAIME))) {
            if(!caughtOrb1){
                monkeySpanks -=5;
                hitsText.setText(monkeySpanks + "% SPANKED");
                caughtOrb1 = true;
            }
                
        } else if ((event.getNodeA().getName().equals(JAIME)
                && event.getNodeB().getName().equals(ORB_TWO))
                || (event.getNodeA().getName().equals(ORB_TWO)
                && event.getNodeB().getName().equals(JAIME))) {
            if (caughtOrb1){
                if(!caughtOrb2){
                    monkeySpanks -= 10;
                    hitsText.setText(monkeySpanks + "% SPANKED");
                    caughtOrb2 = true;
                }
            }
                
        } else if ((event.getNodeA().getName().equals(JAIME)
                && event.getNodeB().getName().equals(ORB_THREE))
                || (event.getNodeA().getName().equals(ORB_THREE)
                && event.getNodeB().getName().equals(JAIME))) {
            if (caughtOrb1 && caughtOrb2){
                if(!caughtOrb3){
                    monkeySpanks -= 15;
                    hitsText.setText(monkeySpanks + "% SPANKED");
                    caughtOrb3 = true;
                }
            }
        } else if ((event.getNodeA().getName().equals(JAIME)
                && event.getNodeB().getName().equals(ORB_FOUR))
                || (event.getNodeA().getName().equals(ORB_FOUR)
                && event.getNodeB().getName().equals(JAIME))) {
            if (caughtOrb1 && caughtOrb2 && caughtOrb3){
                if(!caughtOrb4){
                    monkeySpanks -= 20;
                    hitsText.setText(monkeySpanks + "% SPANKED");
                    caughtOrb4 = true;
                }
            }

        } else if ((event.getNodeA().getName().equals(JAIME)
                && event.getNodeB().getName().equals(BULLET))
                || (event.getNodeA().getName().equals(BULLET)
                && event.getNodeB().getName().equals(JAIME))) {
            monkeySpanks += JAMIE_POINTS;
            hitsText.setText(monkeySpanks + "% SPANKED");
            jamieControl.setPhysicsLocation(new Vector3f(FastMath.nextRandomInt(-120, 120),
                                                             30,
                                                             FastMath.nextRandomInt(-120, 120)));
            
        } else if ((event.getNodeA().getName().equals(BULLET)
                && event.getNodeB().getName().equals(ORB_ONE))
                || (event.getNodeA().getName().equals(ORB_ONE)
                && event.getNodeB().getName().equals(BULLET))) {
            onePoints += ONE_POINTS;
            orb1.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f(FastMath.nextRandomInt(-120, 120),
                                                             20,
                                                             FastMath.nextRandomInt(-120, 120)));
        } else if ((event.getNodeA().getName().equals(BULLET)
                && event.getNodeB().getName().equals(ORB_TWO))
                || (event.getNodeA().getName().equals(ORB_TWO)
                && event.getNodeB().getName().equals(BULLET))) {
            twoPoints += TWO_POINTS;
            orb2.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f(FastMath.nextRandomInt(-120, 120),
                                                             20,
                                                             FastMath.nextRandomInt(-120, 120)));
        } else if ((event.getNodeA().getName().equals(BULLET)
                && event.getNodeB().getName().equals(ORB_THREE))
                || (event.getNodeA().getName().equals(ORB_THREE)
                && event.getNodeB().getName().equals(BULLET))) {
            threePoints += THREE_POINTS; 
            orb3.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f(FastMath.nextRandomInt(-120, 120),
                                                             20,
                                                             FastMath.nextRandomInt(-120, 120)));
        } else if ((event.getNodeA().getName().equals(BULLET)
                && event.getNodeB().getName().equals(ORB_FOUR))
                || (event.getNodeA().getName().equals(ORB_FOUR)
                && event.getNodeB().getName().equals(BULLET))) {
            fourPoints += FOUR_POINTS;
            orb4.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f(FastMath.nextRandomInt(-120, 120),
                                                             20,
                                                             FastMath.nextRandomInt(-120, 120)));
        } //end if

    } //end collision
    
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals(SHOOT) && !keyPressed) {
                shoot();
            }
        }
    };
    
    public void shoot() {
        
        Sphere bullet = new Sphere(22, 22, 0.4f, true, false);
        bullet.setTextureMode(Sphere.TextureMode.Projected);
        Material mat2 = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key2 = new TextureKey("Textures/Terrain/Rock/Rock.PNG");
        key2.setGenerateMips(true);
        Texture tex2 = getAssetManager().loadTexture(key2);
        mat2.setTexture("ColorMap", tex2);
        
        /**
         * Create a cannon ball geometry and attach to scene graph.
         */
        Geometry bulletGeo = new Geometry("bullet", bullet);
        bulletGeo.setMaterial(mat2);
        bulletGeo.setLocalTranslation(cam.getLocation());
        rootNode.attachChild(bulletGeo);
        /**
         * Create physical cannon ball and add to physics space.
         */
        bulletRBC = new RigidBodyControl(.5f);
        bulletGeo.addControl(bulletRBC);
        bulletAppState.getPhysicsSpace().add(bulletRBC);
        bulletRBC.setCcdSweptSphereRadius(.1f);
        bulletRBC.setCcdMotionThreshold(0.001f);
        /**
         * Accelerate the physical ball in camera direction to shoot it!
         */
        bulletRBC.setLinearVelocity(cam.getDirection().mult(50));
        bulletCount++;
    }
    
    
   /**
    * writes the contents of the positionArray to a csv file
    * @param array
    * @param fileName 
    */
   public void positionArrayToFile(String[][] array, String fileName){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            bw.write("JAMIE, ORB 1, ORB 2, ORB 3\n");
            for(int i = 0; i < 100; i++){                 
                bw.write(array[i][0] + "," + 
                         array[i][1] + "," + 
                         array[i][2] + "," + 
                         array[i][3] + "\n");
            } //end for
	} catch (IOException e) {
            e.printStackTrace();
	} //end try
    } //end resultsToFile
   
   /**
    * @return Boolean indicating that the player has shot the
    * monkey a predetermined number of times
    */
   private Boolean monkeySpanked(){
       if (monkeySpanks >= 100){
           return true;
       }
       return false;       
   } //end monkeySpanked
   
   /**
    * Retrieves high scores from txt file and inserts 
    * them into scoreArray
    */
   private void scoreFileToArray(){
       //read high scores from text file into array
       String line;
       int i = 0;
       
       try (BufferedReader br = new BufferedReader(new FileReader(scoreFile))) {

            while ((line = br.readLine()) != null && i < 5) {
                String[] lineSplit = line.split(":");
                scoreArray[i][0] = lineSplit[0];
                scoreArray[i][1] = lineSplit[1];
                i++;
            }

        } catch (IOException e) {
                e.printStackTrace();
        }

   }
   
   /**
    * writes the contents of the scoreArray to a txt file
    */
   private void scoreArrayToFile(){
       
       try (BufferedWriter bw = new BufferedWriter(new FileWriter(scoreFile))) {
           
            for(int i = 0; i < scoreArray.length; i++){                 
                bw.write(scoreArray[i][0] + ":" + 
                         scoreArray[i][1] + "\n");
            } //end for
	} catch (IOException e) {
            e.printStackTrace();
	} //end try
       
   }
   
   /**
    * @return String representation of values stored in scoreArray 
    */
   private String scoreArrayToString(){
       
       String line;
       
       StringBuilder sb = new StringBuilder();
       sb.append("HIGH SCORES");
       
       for(int i = 0; i < scoreArray.length; i++){
           line = "\n" + scoreArray[i][0] + ":  " + scoreArray[i][1];
           sb.append(line);
       }
       
       line = sb.toString();
       return line;
       
   }

   //method to set the current player's initials
   private void setPLayerInitials(String initials){
       this.playerInitials = initials;       
   }
   
   /**
    * compares the current game's score to the top
    * five scores and adjusts the top five scores
    * as necessary
    */
   private void updateScoreArray(){
       
       boolean scoreAdded = false;
       int x = 0;
       for(int i = 0; i < scoreArray.length; i++){
           
           /* If new score has already been added,
            * fill the rest of the array with
            * the old scores.
            * Else check to see if the next old
            * score is less than the current game
            * score.
            * If so insert new score at this position
            * and move old scores down one place
            */
           if(scoreAdded){
               newScoreArray[i][0] = scoreArray[x][0];
               newScoreArray[i][1] = scoreArray[x][1];
           } else {
               if (gameScore < Integer.parseInt(scoreArray[x][1])){
                   newScoreArray[i][0] = scoreArray[x][0];
                   newScoreArray[i][1] = scoreArray[x][1];
               } else {
                   newScoreArray[i][0] = playerInitials;
                   newScoreArray[i][1] = String.valueOf(gameScore);
                   scoreAdded = true;
                   x--;
               }
           }    
           x++;
       }
       
       //update scoreArray with new scores
       for(int i = 0; i < newScoreArray.length; i++){
           scoreArray[i][0] = newScoreArray[i][0];
           scoreArray[i][1] = newScoreArray[i][1];
       }
       
   }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
