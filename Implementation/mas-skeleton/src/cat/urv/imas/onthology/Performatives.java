/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.onthology;

import jade.lang.acl.ACLMessage;

/**
 *
 * @author Ihcrul
 */
public class Performatives extends ACLMessage {
    
    public static final int REQUEST_GAME = 100;
    public static final int REPLY_GAME = 101;
    
    public static final int REQUEST_PLAN_SCOUTS = 200;
    public static final int REPLY_PLAN_SCOUTS = 201;
    public static final int REQUEST_LOCATION_SCOUT = 202;
    public static final int REPLY_LOCATION_SCOUT = 203;
    
    public static final int REQUEST_PLAN_HARVESTERS = 300;
    public static final int REPLY_PLAN_HARVESTERS = 301;
    public static final int REQUEST_PLAN_HARVESTER = 302;
    public static final int REPLY_PLAN_HARVESTER = 303;
    public static final int INFORM_PICKUP = 310;
    public static final int INFORM_NEGOTIATION_DONE = 333;
    
    public static final int REQUEST_VEHICLE_ACTIONS = 400;
    public static final int REPLY_VEHICLE_ACTIONS = 401;
}
