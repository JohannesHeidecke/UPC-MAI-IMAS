/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.plan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ihcrul
 */
public class Plan implements Serializable {
    
    private List<Action> actions = new ArrayList<>();
    
    public void addAction(Action action) {
        this.actions.add(action);
    }
    
    public List<Action> getActions() {
        return this.actions;
    }
    
    
}
