// Part of the Software-Hardware Interface, this class has methods which communicate with the hardware
// Will most likely use Java libraries for accessing the serial port
//
// Depended on by most of the project, in particular Sensor and Pathing
// Depends on the chosen library and the communications link, as well as the corresponding Verilog module
package bravo.game;
public abstract class HWInterface {

public final int NORTHWEST=1,NORTH=2,NORTHEAST=3,EAST=4,SOUTHEAST=5,SOUTH=6,SOUTHWEST=7,WEST=8,CENTRE=0;
//Based on the convention used in Pathing

// Constructor should not need to take any arguments

// Moves head in given direction.  Use constants as described above.
// Returns true when completed
public abstract boolean moveHead (int direction);

// Turns the electromagnet to the On setting
// Returns true when completed
public abstract boolean magnetOn ();

// Turns the electromagnet off
// Returns true when completed
public abstract boolean magnetOff ();

// Returns current status of magnet
// Could just be stored locally
public abstract boolean getMagnetStatus ();

// Resets the read head to a known square and updates local storage of this value
// Turns off electromagnet if on
// Uses touch switches installed to detect when head is reaching extremities of motion
// Returns true when done
public abstract boolean resetHead ();

// Returns current square of magnet head
// Could just store value locally
// See Requirements Specification for integer values
public abstract int getSquare ();

// Returns a 100-element array of sensor output
// If reed switches are significantly offset from magnet then will require this to be done on the hardware side, as moveHead movements will not center the sensors on the squares.
// Otherwise could just create helper methods to read off a rpackage projectBravo;ow of sensor outputs, and use resetHead / moveHead to control movement
// Array should contain true for presence of a piece or false otherwise
// Array key should correspond to integer representation of squares - see requirements specification
public abstract boolean[] sensorOutput ();

}
