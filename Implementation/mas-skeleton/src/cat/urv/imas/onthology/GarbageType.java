/**
 * IMAS base code for the practical work. 
 * Copyright (C) 2016 DEIM - URV
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package cat.urv.imas.onthology;

/**
 * Type of garbage.
 * It provides a way of representing this type of garbage with a single letter.
 */
public enum GarbageType {
    GLASS {
        @Override
        public String getShortString() {
            return "G";
        }
    },
    PAPER {
        @Override
        public String getShortString() {
            return "P";
        }
    },
    PLASTIC {
        @Override
        public String getShortString() {
            return "L";
        }
    };
    
    /**
     * Gets a letter representation of this type of garbage.
     * @return String a single letter representing the type of garbage.
     */
    public abstract String getShortString();
    
    /**
     * Gets the GarbageType according to its short string.
     * @param String type of garbage in short string format.
     * @return The type of of garbage.
     */
    public static GarbageType fromShortString(String type) {
        switch (type) {
            case "G": return GLASS;
            case "P": return PAPER;
            case "L": return PLASTIC;
            default:
                throw new IllegalArgumentException("Garbage type " + type + " is not supported.");
        }
    }
}
