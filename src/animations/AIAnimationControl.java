/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package animations;

import AI.AIControl;
import characters.AICharacterControl;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.collision.CollisionResults;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Line;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.jme3.asset.AssetManager;

/**
 *
 * @author Prof Wireman
 */
public class AIAnimationControl extends AbstractControl implements AnimEventListener, ActionListener, AnalogListener {
    
    private AnimControl animControl;
    private AnimChannel upperChannel;
    private AnimChannel lowerChannel;
    
    //check to see if AI is moving
    private float aiX = 0f;
    private float aiNewX = 0f;
    private float aiZ = 0f;
    private float aiNewZ = 0f;
    
    //count cycles
    private int updateCount = 0;
    
    //targets
    private Spatial target;
    private List<Spatial> targetableObjects = new ArrayList<Spatial>();
    
    boolean forward, backward, leftRotate, rightRotate, leftStrafe, rightStrafe, jumpStarted, inAir, punching, moving;
    private Properties animationNames;

    public enum Animation{
        Idle("idle", LoopMode.Loop, 0.2f),
        Walk("walk", LoopMode.Loop, 0.2f),
        Run("run", LoopMode.Loop, 0.2f),
        Punch("attack1", LoopMode.Loop, 0.2f);
        
        Animation(String key, LoopMode loopMode, float blendTime){
            this.key = key;
            this.loopMode = loopMode;
            this.blendTime = blendTime;
        }
        
        String key;
        LoopMode loopMode;
        float blendTime;
    }
    
    public enum Channel{
        Upper,
        Lower,
        All,
    }
    
     public AIAnimationControl() {
        
    }
    public AIAnimationControl(String animationNameFile){
        animationNames = new Properties();
        try {
            animationNames.load(getClass().getClassLoader().getResourceAsStream(animationNameFile));
        } catch (IOException ex) {
            Logger.getLogger(AdvAnimationManagerControl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public String getAnimationName(String key){
        String animName = animationNames.getProperty(key);
        if(animName != null){
            return animName;
        }
        return key;
    }
    @Override
    protected void controlUpdate(float tpf) {
        
        updateCount++;
        
        if (updateCount % 10 == 0){
            
            aiX = aiNewX;
            aiZ = aiNewZ;
            aiNewX = Float.parseFloat(String.format("%.4f", spatial.getLocalTranslation().x));
            aiNewZ = Float.parseFloat(String.format("%.4f", spatial.getLocalTranslation().z));
            
            if (aiNewX == aiX && aiNewZ == aiZ) {
                moving = false;
            } else {
                moving = true;
            } //end if
            
            if (!moving) {
                if (lowerChannel.getAnimationName() == null || !lowerChannel.getAnimationName().equals("Idle")){
                    setAnimation(Animation.Idle, Channel.All);
                }
            } else {
                if (lowerChannel.getAnimationName() == null || !lowerChannel.getAnimationName().equals("Run")){
                    setAnimation(Animation.Run);
                }
            } //end if
            
            if(sense()){
                if(!punching){
                    setAnimation(Animation.Punch, Channel.Upper);
                    punching = true;
                    System.out.println("Punching");
                }
            } else {
                if(moving){
                    setAnimation(Animation.Run, Channel.Upper);
                } else {
                    setAnimation(Animation.Idle, Channel.Upper);
                }
                punching = false;
                //System.out.println("nope");
            }
            
        } //end if
        
    }
    
    public void setTargetList(List<Spatial> objects){
        targetableObjects = objects;
    }
    
    private Vector3f viewDirection = new Vector3f(1, 0, 0);
    
    private float sightRange = 10f;
    private float angle = FastMath.PI;
    private Geometry[] sightLines = new Geometry[300];
    private boolean debug = false;
    
    private boolean sense(){
        target = null;
        Quaternion aimDirection = new Quaternion();
        Vector3f rayDirection = new Vector3f();
        int i = 0;
        boolean foundTarget = false;
        for(float x = -angle; x < angle; x+= FastMath.PI * 0.05f){
            if(debug && sightLines[i] != null){
                ((Node)getSpatial().getParent()).detachChild(sightLines[i]);
            }
            rayDirection.set(viewDirection);
            aimDirection.fromAngleAxis(x, Vector3f.UNIT_Y);
            aimDirection.multLocal(rayDirection);
            Ray ray = new Ray(spatial.getWorldTranslation().add(0, 1f, 0), rayDirection);
            ray.setLimit(sightRange);
            CollisionResults col = new CollisionResults();
            for(Spatial s: targetableObjects){
                s.collideWith(ray, col);
            }
            
            if(col.size() > 0){
                target = col.getClosestCollision().getGeometry();
                foundTarget = true;
                break;
            }
            
            if(debug){
                Geometry line = makeDebugLine(ray);
                sightLines[i++] = line;
                ((Node)getSpatial().getParent()).attachChild(line);
            }
        }
        return foundTarget;
    }
    
    private Geometry makeDebugLine(Ray r){
        Line l = new Line(r.getOrigin(), r.getOrigin().add(r.getDirection().mult(sightRange)));
        Geometry line = new Geometry("", l);
        line.setMaterial(mygame.Main.lineMat);
        return line;
    }
    
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
    
    @Override
    public Control cloneForSpatial(Spatial spatial) {
        AIAnimationControl control = new AIAnimationControl();
        control.animationNames = animationNames;
        return control;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        animControl = spatial.getControl(AnimControl.class);
        upperChannel = animControl.createChannel();
        lowerChannel = animControl.createChannel();
        upperChannel.addFromRootBone("spine");
        lowerChannel.addBone("Root");
        lowerChannel.addFromRootBone("pelvis");

        animControl.addListener(this);
    }
    
    public void setAnimation(Animation animation, Channel channel){
        System.out.println(" " + animation);
        switch(channel){
            case Upper:
                setAnimation(animation, upperChannel);
                break;
            case Lower:
                setAnimation(animation, lowerChannel);
                break;
            case All:
                setAnimation(animation, upperChannel);
                setAnimation(animation, lowerChannel);
                break;
        }
    }
    
    private void setAnimation(Animation animation, AnimChannel channel) {
        if(channel.getAnimationName() == null || !channel.getAnimationName().equals(animation.name())){
            channel.setAnim(getAnimationName(animation.key), animation.blendTime);
        }
        
        channel.setLoopMode(animation.loopMode);
                
    }
    
    public void setAnimation(Animation animation){
        setAnimation(animation, upperChannel);
        setAnimation(animation, lowerChannel);
    }
    
    //added
    public String getAnimation(AnimChannel channel){
        return spatial.getLocalTranslation().toString();
    }

    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        if(channel.getLoopMode() == LoopMode.DontLoop){

            Animation newAnim = Animation.Idle;
            Animation anim = Animation.valueOf(animName);
            
            setAnimation(newAnim, channel);
        }
    }

    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
    }
    
    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("MoveForward")) {
            forward = value;
        } else if (binding.equals("MoveBackward")) {
            backward = value;
        }
        
    }

    public void onAnalog(String name, float value, float tpf) {
        
    }
}
