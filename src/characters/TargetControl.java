/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package characters;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class TargetControl extends BetterCharacterControl {
    
    protected Node head = new Node("Head");
    
    public TargetControl(float radius, float height, float mass) {
        super(radius, height, mass);
        head.setLocalTranslation(0, 1.8f, 0);
    }
    
    public void setPhysicsLocation(Vector3f vec){
        super.setPhysicsLocation(vec);
    }
 
}
