
package physics;


import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;
import com.jme3.texture.Texture;

/**
 *
 * @author normenhansen
 */
public class PhysicsTestHelper {
    
    private int bulletCount = 0;
    
    public static void createWorld(Node rootNode, AssetManager assetManager, PhysicsSpace space) {
        DirectionalLight light = new DirectionalLight();
        //AmbientLight light = new AmbientLight();
        light.setColor(ColorRGBA.LightGray);
        rootNode.addLight(light);

        Material boxMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        boxMaterial.setTexture("ColorMap", assetManager.loadTexture("Materials/darkBricks.png"));
        
        Material ballMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        ballMaterial.setTexture("ColorMap", assetManager.loadTexture("Materials/diamond.jpg"));
        
        Material wallMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        wallMaterial.setTexture("ColorMap", assetManager.loadTexture("Materials/green.jpeg"));
                
        Box xWall = new Box(.25f, 50, 125);
        Box yWall = new Box(125, .25f, 125);
        Box zWall = new Box(125, 50, .25f);
        
        Geometry floorGeometry = new Geometry("Floor", yWall);
        floorGeometry.setMaterial(wallMaterial);
        floorGeometry.setLocalTranslation(0, -0.26f, 0);
        floorGeometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(floorGeometry);
        space.add(floorGeometry);
        
        Geometry ceilingGeometry = new Geometry("Ceiling", yWall);
        ceilingGeometry.setMaterial(wallMaterial);
        ceilingGeometry.setLocalTranslation(0, 100, 0);
        ceilingGeometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(ceilingGeometry);
        space.add(ceilingGeometry);
        
        Geometry leftWallGeom = new Geometry("leftWall", xWall);
        leftWallGeom.setMaterial(wallMaterial);
        leftWallGeom.setLocalTranslation(-125, 50, 0);
        leftWallGeom.addControl(new RigidBodyControl(0));
        rootNode.attachChild(leftWallGeom);
        space.add(leftWallGeom);
        
        Geometry rightWallGeom = new Geometry("rightWall", xWall);
        rightWallGeom.setMaterial(wallMaterial);
        rightWallGeom.setLocalTranslation(125, 50, 0);
        rightWallGeom.addControl(new RigidBodyControl(0));
        rootNode.attachChild(rightWallGeom);
        space.add(rightWallGeom);
        
        Geometry frontWallGeom = new Geometry("frontWall", zWall);
        frontWallGeom.setMaterial(wallMaterial);
        frontWallGeom.setLocalTranslation(0, 50, 125);
        frontWallGeom.addControl(new RigidBodyControl(0));
        rootNode.attachChild(frontWallGeom);
        space.add(frontWallGeom);
        
        Geometry rearWallGeom = new Geometry("rearWall", zWall);
        //rearWallGeom.setQueueBucket(Bucket.Transparent);
        rearWallGeom.setMaterial(wallMaterial);
        rearWallGeom.setLocalTranslation(0, 50, -125);
        rearWallGeom.addControl(new RigidBodyControl(0));
        rootNode.attachChild(rearWallGeom);
        space.add(rearWallGeom);

        //movable spheres
        for (int i = 0; i < 5; i++) {
            Sphere sphere = new Sphere(16, 16, .5f);
            Geometry ballGeometry = new Geometry("Soccer ball", sphere);
            ballGeometry.setMaterial(ballMaterial);
            ballGeometry.setLocalTranslation(i-2.5f, 2, -50);
            //RigidBodyControl automatically uses Sphere collision shapes when attached to single geometry with sphere mesh
            ballGeometry.addControl(new RigidBodyControl(.001f));
            ballGeometry.getControl(RigidBodyControl.class).setRestitution(1);
            rootNode.attachChild(ballGeometry);
            space.add(ballGeometry);
        }
        {
        //immovable Box with mesh collision shape
        Box box = new Box(2, 2, 2);
        Geometry boxGeometry = new Geometry("Box", box);
        boxGeometry.setMaterial(boxMaterial);
        boxGeometry.setLocalTranslation(20, 2, -20);
        boxGeometry.addControl(new RigidBodyControl(new MeshCollisionShape(box), 0));
        rootNode.attachChild(boxGeometry);
        space.add(boxGeometry);
        }
        {
        //immovable Box with mesh collision shape
        Box box = new Box(2, 2, 2);
        Geometry boxGeometry = new Geometry("Box", box);
        boxGeometry.setMaterial(boxMaterial);
        boxGeometry.setLocalTranslation(10, 2, -50);
        boxGeometry.addControl(new RigidBodyControl(new MeshCollisionShape(box), 0));
        rootNode.attachChild(boxGeometry);
        space.add(boxGeometry);
        }
        {
        //immovable Box with mesh collision shape
        Box box = new Box(2, 2, 2);
        Geometry boxGeometry = new Geometry("Box", box);
        boxGeometry.setMaterial(boxMaterial);
        boxGeometry.setLocalTranslation(-20, 2, -20);
        boxGeometry.addControl(new RigidBodyControl(new MeshCollisionShape(box), 0));
        rootNode.attachChild(boxGeometry);
        space.add(boxGeometry);
        }
        {
        //immovable Box with mesh collision shape
        Box box = new Box(2, 2, 2);
        Geometry boxGeometry = new Geometry("Box", box);
        boxGeometry.setMaterial(boxMaterial);
        boxGeometry.setLocalTranslation(-10, 2, -50);
        boxGeometry.addControl(new RigidBodyControl(new MeshCollisionShape(box), 0));
        rootNode.attachChild(boxGeometry);
        space.add(boxGeometry);
        }
        {
        //immovable Box with mesh collision shape
        Box box = new Box(2, 2, 2);
        Geometry boxGeometry = new Geometry("Box", box);
        boxGeometry.setMaterial(boxMaterial);
        boxGeometry.setLocalTranslation(0, 2, -100);
        boxGeometry.addControl(new RigidBodyControl(new MeshCollisionShape(box), 0));
        rootNode.attachChild(boxGeometry);
        space.add(boxGeometry);
        }
    }

    /**
     * creates the necessary inputlistener and action to shoot balls from teh camera
     * @param app
     * @param rootNode
     * @param space
     */
//    public static void createBallShooter(final SimpleApplication app, final Node rootNode, final PhysicsSpace space) {
//        
//        ActionListener actionListener = new ActionListener() {
//
//           public void onAction(String name, boolean keyPressed, float tpf) {
//                Sphere bullet = new Sphere(22, 22, 0.4f, true, false);
//                bullet.setTextureMode(TextureMode.Projected);
//                Material mat2 = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
//                TextureKey key2 = new TextureKey("Textures/Terrain/Rock/Rock.PNG");
//                key2.setGenerateMips(true);
//                Texture tex2 = app.getAssetManager().loadTexture(key2);
//                mat2.setTexture("ColorMap", tex2);
//                if (name.equals("shoot") && !keyPressed) {
//                    Geometry bulletg = new Geometry("bullet", bullet);
//                    bulletg.setMaterial(mat2);
//                    bulletg.setShadowMode(ShadowMode.CastAndReceive);
//                    bulletg.setLocalTranslation(app.getCamera().getLocation());
//                    RigidBodyControl bulletControl = new RigidBodyControl(10);
//                    bulletg.addControl(bulletControl);
//                    bulletControl.setLinearVelocity(app.getCamera().getDirection().mult(50));
//                    bulletg.addControl(bulletControl);
//                    rootNode.attachChild(bulletg);
//                    space.add(bulletControl);
//                    //bulletCount++;
//                  
//                }
//            }
//        };
//        app.getInputManager().addMapping("shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
//        app.getInputManager().addListener(actionListener, "shoot");
//        
//    }
}
