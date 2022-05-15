/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package animations;

//import InputAppState.*;
//Import appstate.InputAppState;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResults;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.Joystick;
import com.jme3.input.JoystickAxis;
import com.jme3.input.JoystickButton;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
//import chapter02.control.ChaseCamCharacter;
import characters.MyGameCharacterControl;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Prof Wireman
 */
public class CharacterInputAnimationAppState extends AbstractAppState implements AnalogListener, ActionListener{
    
    private Application app;
    private InputManager inputManager;
    private float sensitivity = 100;
    
    private List<ActionListener> actionListeners = new ArrayList<ActionListener>();
    private List<AnalogListener> analogListeners = new ArrayList<AnalogListener>();
    
    /**
     * 
     */
    private ChaseCamera chaseCam;
    /**
     * 
     */

    public enum InputMapping{
        LeanLeft,
        LeanRight,
        LeanFree,
        ToggleCover,
        RotateLeft,
        RotateRight,
        LookUp,
        LookDown,
        StrafeLeft,
        StrafeRight,
        MoveForward,
        MoveBackward,
        Fire,
        Jump;
    }
    
    private String[] mappingNames = new String[]{InputMapping.Fire.name(), InputMapping.LeanFree.name(), InputMapping.LeanLeft.name(), InputMapping.LeanRight.name(),InputMapping.ToggleCover.name(), InputMapping.RotateLeft.name(), InputMapping.RotateRight.name(), InputMapping.LookUp.name(), InputMapping.LookDown.name(), InputMapping.StrafeLeft.name(),
        InputMapping.StrafeRight.name(), InputMapping.MoveForward.name(), InputMapping.MoveBackward.name(), ChaseCamera.ChaseCamUp, ChaseCamera.ChaseCamDown, ChaseCamera.ChaseCamMoveLeft, ChaseCamera.ChaseCamMoveRight, InputMapping.Jump.name()};
    
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = app;
        this.inputManager = app.getInputManager();
        addInputMappings();
        assignJoysticks();
    }
    
    private void addInputMappings(){
        inputManager.addMapping(InputMapping.LeanFree.name(), new KeyTrigger(KeyInput.KEY_V));
        inputManager.addMapping(InputMapping.LeanLeft.name(), new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping(InputMapping.LeanRight.name(), new KeyTrigger(KeyInput.KEY_E));
        inputManager.addMapping(InputMapping.StrafeLeft.name(), new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping(InputMapping.StrafeRight.name(), new KeyTrigger(KeyInput.KEY_L));
        inputManager.addMapping(InputMapping.MoveForward.name(), new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping(InputMapping.MoveBackward.name(), new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping(InputMapping.Jump.name(), new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping(InputMapping.Fire.name(), new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, mappingNames);
        
    }
    
    private void assignJoysticks(){
        Joystick[] joysticks = inputManager.getJoysticks();
        if (joysticks != null){
            for( Joystick j : joysticks ) {
                for(JoystickAxis axis : j.getAxes()){
                    if(axis == j.getXAxis()){
                        axis.assignAxis(InputMapping.StrafeRight.name(), InputMapping.StrafeLeft.name());
                    } else if ( axis == j.getYAxis()){
                        axis.assignAxis(InputMapping.MoveBackward.name(), InputMapping.MoveForward.name());
                    } else if (axis.getLogicalId().equals("ry")){
                        axis.assignAxis(ChaseCamera.ChaseCamUp, ChaseCamera.ChaseCamDown);
                    } else if(axis.getLogicalId().equals("rx")){
                        axis.assignAxis(ChaseCamera.ChaseCamMoveRight, ChaseCamera.ChaseCamMoveLeft);
                    }
                }
                
                for(JoystickButton button : j.getButtons()){
                    button.assignButton("Fire");
                }
            }

            
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();
        for (InputMapping i : InputMapping.values()) {
            if (inputManager.hasMapping(i.name())) {
                inputManager.deleteMapping(i.name());
            }
        }
        inputManager.removeListener(this);
    }
    
    public void onAnalog(String name, float value, float tpf) {
        for(AnalogListener analogListener: analogListeners){
            analogListener.onAnalog(name, value * sensitivity, tpf);
        }
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        for(ActionListener actionListener: actionListeners){
            actionListener.onAction(name, isPressed, tpf);
        }
    }
    
    public void setChaseCamera(ChaseCamera cam){
        this.chaseCam = cam;
    }
  
    public void addActionListener(ActionListener listener){
        actionListeners.add(listener);
    }
    
    public void addAnalogListener(AnalogListener listener){
        analogListeners.add(listener);
    }
    
    public void removeActionListener(ActionListener listener){
        actionListeners.remove(listener);
    }
    
    public void removeAnalogListener(AnalogListener listener){
        analogListeners.remove(listener);
    }
}
