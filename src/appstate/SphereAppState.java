/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package appstate;

import characters.TargetControl;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;

/**
 *
 * @author melms 
 * modified by dustinoakes
 */
public class SphereAppState extends AbstractAppState {

    private SimpleApplication app;
    private AssetManager assetManager;
    private Node rootNode;
    private PhysicsSpace space;

    private static Sphere sphere = new Sphere(16, 16, 1f);
    
    /**
     * PhysicsControls for cube.
     */
    private RigidBodyControl orbRBC;
    
    public void setSpace(PhysicsSpace space){
        this.space = space;        
    }

    public Geometry orb(String name, Vector3f loc, ColorRGBA color) {
        Geometry geom = new Geometry(name, sphere);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        geom.setMaterial(mat);
        geom.setLocalTranslation(loc);
        return geom;
    }

    public void makeOrbs(int number) {
        for (int i = 0; i < number; i++) {
            // randomize 3D coordinates
            Vector3f loc = new Vector3f(
                    FastMath.nextRandomInt(-20, 20),
                    FastMath.nextRandomInt(0, 20),
                    FastMath.nextRandomInt(-20, 20));
            Geometry geom = orb("orb" + i, loc, ColorRGBA.randomColor());
            orbRBC = new RigidBodyControl(0.5f);
            //geom.addControl(new RigidBodyControl(new MeshCollisionShape(sphere), 0));
            geom.addControl(orbRBC);
            //geom.addControl(new SphereControl());

            rootNode.attachChild(geom);
            space.add(geom);
        }
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        //TODO: initialize your AppState, e.g. attach spatials to rootNode
        //this is called on the OpenGL thread after the AppState has been attached
        this.app = (SimpleApplication) app;
        this.assetManager = this.app.getAssetManager();
        this.rootNode = this.app.getRootNode();
        //this.space = this.app.
        //this.app.getClass().getField("guiText").set;

        makeOrbs(3);
    }

    @Override
    public void update(float tpf) {
        //TODO: implement behavior during runtime
    }

    @Override
    public void cleanup() {
        super.cleanup();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
    }
}
