/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package appstate;

import AI.AIControl;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author Prof Wireman
 */
public abstract class AIState extends AbstractControl{
    
    protected AIControl aiControl;
    
    public abstract void stateEnter();
    
    public abstract void stateExit();

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        
        aiControl = this.spatial.getControl(AIControl.class);
    }
    
    

    @Override
    public void setEnabled(boolean enabled) {
        if(enabled && !this.enabled){
            stateEnter();
        }else if(!enabled && this.enabled){
            stateExit();
        }
        this.enabled = enabled;
        System.out.println("State " + this + " " + enabled);
    }
    
    
}
