package org.usfirst.frc.team4509.robot;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.IterativeRobot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Compressor;

/*
 * Edited By Kyle Brott on 10/25/17 
 * Cleaned up, mostly just little things. Most likely broke something
 * TODO: JavaDocs
 * Currently on GitHub @ https://github.com/FRC-4509-MechBulls/PizzaBot
 */

public class Robot extends IterativeRobot {

	enum EDriveType {
		StartingRight,
		StartingLeft,
		StartingMiddleRight,
		StartingMiddleLeft,
		FPSTest
	}
	
	/*
	 * CHANGE THIS VALUE TO MATCH WHERE THE ROBOT STARTS| |
	 * Change the value behind the dot,      right here V V
	 * tag:CHANGE_ON_START
	 */
	EDriveType CurrentDriveType = EDriveType.StartingRight;
	
	RobotDrive myRobot = new RobotDrive(0, 1);
	Joystick lStick = new Joystick(0); // Left joystick (May not always be the one on the physical left)
	Joystick rStick = new Joystick(1); // Right joystick (May not always be the one on the physical right)
	Timer timer = new Timer();
	CANTalon talonDriveFrontRight = new CANTalon(0); // The front right driving Talon
	CANTalon talonDriveBackRight  = new CANTalon(3); // The back right driving Talon
	CANTalon talonDriveFrontLeft  = new CANTalon(8); // The front left driving Talon
	CANTalon talonDriveBackLeft   = new CANTalon(5); // The back left driving Talon 
	CANTalon talonRopeClimbRight  = new CANTalon(1); // The right rope climber Talon
	CANTalon talonRopeClimbLeft   = new CANTalon(7);  // The left rope climber Talon
	XboxController xControl = new XboxController(0);
	ADXRS450_Gyro gyro = new ADXRS450_Gyro();
	Solenoid solenoid = new Solenoid(4); // The solenoid used to control the pneumatics system
	Compressor compressor = new Compressor(1); // The compressor used to compress air for the pneumatics system
	
	final double NORMAL_SPEED = 3;
	
	double angle = gyro.getAngle(); // The angle that the robot starts at. Used for straight driving.
	double leftSpeed = NORMAL_SPEED; // The current speed that the left side of the robot should be driving at. Used for straight driving.
	double rightSpeed = -NORMAL_SPEED; // The current speed that the right side of the robot should be driving at. Negative for forward, positive for backward. Used for straight driving
	boolean isChanged = true; // A boolean that is used in the driving straight method.	

	final double INCHES_ROBOT_TRAVELLED = 23; // Change this value with the amount of inches that the robot traveled during the FPSTest tag:CHANGE_ON_START
	final double FEET_PER_SECOND = (INCHES_ROBOT_TRAVELLED / 12) / 2; // The speed in feet per second that the robot can move
	
	double baseSpeed = 4;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {		
		// Change all of the Talons into voltage control mode
		talonDriveBackLeft.changeControlMode(CANTalon.TalonControlMode.Voltage);
		talonDriveBackRight.changeControlMode(CANTalon.TalonControlMode.Voltage);
		talonDriveFrontLeft.changeControlMode(CANTalon.TalonControlMode.Voltage);
		talonDriveFrontRight.changeControlMode(CANTalon.TalonControlMode.Voltage);
		talonRopeClimbRight.changeControlMode(CANTalon.TalonControlMode.Voltage);
		talonRopeClimbLeft.changeControlMode(CANTalon.TalonControlMode.Voltage);
	}

	/**
	 * This function is run once each time the robot enters autonomous mode
	 */
	@Override
	public void autonomousInit() {
		timer.start();
		timer.reset(); // Make sure the timer is at 0
		gyro.calibrate(); // resets gyro position and sets new position as pos. 0
		angle = gyro.getAngle();
		
		if(CurrentDriveType == EDriveType.StartingRight)
			autonomousStartingRightSide();
		else if(CurrentDriveType == EDriveType.StartingLeft)
			autonomousStartingLeftSide();
		else if(CurrentDriveType == EDriveType.StartingMiddleRight)
			autonomousStartingMiddle(true);
		else if(CurrentDriveType == EDriveType.StartingMiddleLeft)
			autonomousStartingMiddle(false);
		else if(CurrentDriveType == EDriveType.FPSTest)
			fpsTester();
	}
	
	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		
	}
	
	// Called to test FPS
	void fpsTester() {
		while(timer.hasPeriodPassed(timer.get() + 2)) {
			talonDriveBackLeft.set(3);
			talonDriveBackRight.set(-3);
			talonDriveFrontLeft.set(3);
			talonDriveFrontRight.set(-3);
		}
		while(timer.hasPeriodPassed(timer.get() + .1f))
		talonDriveBackLeft.set(-1);
		talonDriveBackRight.set(1);
		talonDriveFrontLeft.set(-1);
		talonDriveFrontRight.set(1);
		
	}
	
	// Called when the robot starts on the left
	void autonomousStartingLeftSide() {
		driveForFeet(7.77083);
		turnForDegrees(45);
		driveForFeet(5);
		driveForFeet(-5);
		turnForDegrees(-45);
		driveForFeet(10);
	}
	
	// Called when the robot starts on the right
	void autonomousStartingRightSide() {
		driveForFeet(7.77083);
		turnForDegrees(-45);
		driveForFeet(5);
		driveForFeet(-5);
		turnForDegrees(45);
		driveForFeet(10);
	}
	
	// Called when the robot starts in the middle. Pass in true to go right around the airship, and false for the left side.
	void autonomousStartingMiddle(boolean turnRight) {
		driveForFeet(7.77083);
		driveForFeet(-4);
		if(turnRight) {
			turnForDegrees(90);
			driveForFeet(6);
			turnForDegrees(-45);
			driveForFeet(10);
		}
		else {
			turnForDegrees(-90);
			driveForFeet(6);
			turnForDegrees(45);
			driveForFeet(10);
		}
		
	}
	
	/**
	 * This function is called once each time the robot enters remote mode
	 */
	@Override
	public void teleopInit() {}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
//		myRobot.tankDrive(lStick.getY(), rStick.getY());

		while (isEnabled() && isOperatorControl()){
			SmartDashboard.putString("DB/String 0", "Base speed: " + baseSpeed);
			
			// THROTTLE JOYSTICK
			// throttle wheel is located on right hand joystick
			// The Throttle variant goes from
			// 4 to 8 to 12
			if (rStick.getThrottle() <= -0.25 && rStick.getThrottle() >= -1){// lowest 3rd of throttle wheel
				baseSpeed = 4;
			}
			if (rStick.getThrottle() <= 0.5 && rStick.getThrottle() > -0.25){// middle 3rd of the throttle wheel
				baseSpeed = 8;
			}
			if (rStick.getThrottle() <= 1  && rStick.getThrottle() > 0.5){// top 3rd of throttle wheel
				baseSpeed = 12;
			}
			
			// DRIVE JOYSTICK
			// negative and positive BaseSpd keeps the Drivetrain going the same direction since one has to be inverse
			talonDriveBackLeft.set(lStick.getY() * -baseSpeed);
			talonDriveFrontLeft.set(lStick.getY() * -baseSpeed);
			talonDriveFrontRight.set(rStick.getY() * baseSpeed);
			talonDriveBackRight.set(rStick.getY() * baseSpeed);
			
			// ROPECLIMB JOYSTICK
			if (rStick.getRawButton(1)) { // raw button 1 is the trigger
				talonRopeClimbLeft.set(-4);
				talonRopeClimbRight.set(4);
				SmartDashboard.putString("DB/String 5","Trigger hit");
			} else {
				talonRopeClimbLeft.set(0);
				talonRopeClimbRight.set(0);
			}
			
			
			// Gear Controls
			if(lStick.getRawButton(1))
				solenoid.set(true); // sets the solenoid to push open the gate to collect the gear
			else
				solenoid.set(false); // closes the gate for gear after collecting gear
			
		}
		SmartDashboard.putString("DB/String 0", "TeleOperator is Not Enabled");

	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
		LiveWindow.run();
	}
	
	
	/*
	* This method takes a double parameter, which represents the amount of feet you want the bot to move. It moves the bot the specified amount of feet.
	* Pass in a negative value to go backwards, and positive to go forward. Has a 1 degree margin of error allowed for straight driving.
	*/
	void driveForFeet(double feet) {
		angle = gyro.getAngle(); // Set angle to the current angle so that no matter what the original angle was, the robot will drive straight according to the current angle
		double time = timer.get(); // Set time to the current time
		if(feet > 0) { // If feet is positive
			leftSpeed = 3; // Start driving forward
			rightSpeed = 3; 
			while(!timer.hasPeriodPassed(time + getSeconds(feet))) { // If the amount of time needed to travel the number of feet needed hasn't passed
				while(gyro.getAngle() - angle < -.5d) { // If the angle is too much to the left (.5 degree margin of error allowed)
					if(isChanged) { // Check isChanged. This makes sure that the drive values don't change too drastically.
						leftSpeed += 1;
						rightSpeed += 1;
						isChanged = false;
					}
					
					// Set the talons' speed based on the corrective values
					talonDriveBackLeft.set(leftSpeed);
					talonDriveFrontLeft.set(leftSpeed);
					talonDriveBackRight.set(rightSpeed);
					talonDriveFrontRight.set(rightSpeed);
				}
				while(gyro.getAngle() - angle > .5d) { // If the angle is too far to the right (.5 degree margin of error allowed)
					// Slow down right side and speed up left
					if(isChanged) { // Check isChanged. This makes sure that the drive values don't change too drastically.
						leftSpeed -= 1;
						rightSpeed-= 1;
						isChanged = false;
					}
					
					// Set the talons' speed based on the corrective values
					talonDriveBackLeft.set(leftSpeed);
					talonDriveFrontLeft.set(leftSpeed);
					talonDriveBackRight.set(rightSpeed);
					talonDriveFrontRight.set(rightSpeed);
	
				}
				// Set the speeds back to normal. Called when the robot's angle is within the margin of error
				leftSpeed = NORMAL_SPEED;
				rightSpeed = -NORMAL_SPEED;
				isChanged = true;
				talonDriveBackLeft.set(leftSpeed);
				talonDriveFrontLeft.set(leftSpeed);
				talonDriveBackRight.set(rightSpeed);
				talonDriveFrontRight.set(rightSpeed);
				// SmartDashboard.putString("DB/String 5", "Going Straight");
			}
			
			// supply a quick burst of backward motion to stop faster.
			time = timer.get();
			while(!timer.hasPeriodPassed(time + .2d)) {
				talonDriveBackLeft.set(-2);
				talonDriveFrontLeft.set(-2);
				talonDriveFrontRight.set(2);
				talonDriveBackRight.set(2);
			}
		}
		else if(feet < 0) { // If feet is negative
			leftSpeed = -3; // start driving backward
			rightSpeed = -3;
			while(!timer.hasPeriodPassed(time + getSeconds(feet))) { // If the amount of time needed to travel the number of feet needed hasn't passed

				// If the angle is too much to the left (.5 degree margin of error allowed)
				while(gyro.getAngle() - angle < -.5d) {
					if(isChanged) { // Check isChanged. This makes sure that the drive values don't change too drastically.
						leftSpeed += 1;
						rightSpeed += 1;
						isChanged = false;
					}
					talonDriveBackLeft.set(leftSpeed);
					talonDriveFrontLeft.set(leftSpeed);
					talonDriveBackRight.set(rightSpeed);
					talonDriveFrontRight.set(rightSpeed);
				}

				// If the angle is too much to the right (.5 degree margin of error allowed)
				while(gyro.getAngle() - angle > .5d) {
					if(isChanged) { // Check isChanged. This makes sure that the drive values don't change too drastically.
						leftSpeed -= 1;
						rightSpeed -= 1;
						isChanged = false;
					}
					talonDriveBackLeft.set(leftSpeed);
					talonDriveFrontLeft.set(leftSpeed);
					talonDriveBackRight.set(rightSpeed);
					talonDriveFrontRight.set(rightSpeed);
				}
				
				// Set the speeds back to normal. Called when the robot's angle is within the margin of error
				leftSpeed = -NORMAL_SPEED;
				rightSpeed = NORMAL_SPEED;
				isChanged = true;
				talonDriveBackLeft.set(leftSpeed);
				talonDriveFrontLeft.set(leftSpeed);
				talonDriveBackRight.set(rightSpeed);
				talonDriveFrontRight.set(rightSpeed);
				// SmartDashboard.putString("DB/String 5", "Going Straight");
			}
			
			// Supply quick burst of forward motion to make the robot stop faster.
			time = timer.get();
			while(!timer.hasPeriodPassed(time + .2d)) {
				talonDriveBackLeft.set(3);
				talonDriveFrontLeft.set(3);
				talonDriveFrontRight.set(-3);
				talonDriveBackRight.set(-3);
			}
		}
		
		// Stop the robot
		talonDriveBackLeft.set(0);
		talonDriveFrontLeft.set(0);
		talonDriveBackRight.set(0);
		talonDriveFrontRight.set(0);
	}
	
	/*
	 * This method will make the robot turn x amount of degrees. The parameter can accept negative numbers.
	 * Positive numbers make the robot turn to the right, and negative numbers make it turn to the left.
	 * Has a 1 degree margin of error
	 */
	void turnForDegrees(double degreesToTurn) {
		double startingAngle = gyro.getAngle(); // The angle that the robot starts at
		double targetAngle = gyro.getAngle() + degreesToTurn; // The angle the robot wants to end at
		while(startingAngle + degreesToTurn - .5d > gyro.getAngle() || startingAngle + degreesToTurn + .5d < gyro.getAngle()) { // if the angle isn't the correct angle
			if(gyro.getAngle() - targetAngle > .5f) { // If the current angle is too far right
				talonDriveBackLeft.set(-3.5d);
				talonDriveFrontLeft.set(-3.5d);
				talonDriveBackRight.set(-3.5d);
				talonDriveFrontRight.set(-3.5d);
			} else if(gyro.getAngle() - targetAngle < -.5f) { // If the current angle is too far left
				talonDriveBackLeft.set(3.5d);
				talonDriveFrontLeft.set(3.5d);
				talonDriveBackRight.set(3.5d);
				talonDriveFrontRight.set(3.5d);
			}
		}
		
		// Stop the robot
		talonDriveBackLeft.set(0);
		talonDriveFrontLeft.set(0);
		talonDriveBackRight.set(0);
		talonDriveFrontRight.set(0);
	}
	
	// Returns the amount of seconds needed in order to travel the distance that's passed in
	double getSeconds(double distance) {
		return Math.abs(distance / FEET_PER_SECOND);
	}

}