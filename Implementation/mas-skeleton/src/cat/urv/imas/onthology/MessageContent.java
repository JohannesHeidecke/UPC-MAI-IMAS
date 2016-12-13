/**
 *  IMAS base code for the practical work.
 *  Copyright (C) 2014 DEIM - URV
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cat.urv.imas.onthology;

/**
 * Content messages for inter-agent communication.
 */
public class MessageContent {
    
    /**
     * Message sent from Coordinator agent to System agent to get the whole
     * city information.
     */
    public static final String REQUEST_GAME = "Get map";
    public static final String REPLY_GAME = "Reply game";
    
    public static final String REQUEST_PLAN_SCOUTS = "Get scouting plan";
    public static final String REPLY_PLAN_SCOUTS = "Reply scouting plan";
    
    public static final String REQUEST_PLAN_HARVESTERS = "Get harvesting plan";
    public static final String REPLY_PLAN_HARVESTERS = "Reply harvesting plan";
    
    public static final String REQUEST_VEHICLE_ACTIONS = "Request vehicle actions";
    
    
    
    
}
