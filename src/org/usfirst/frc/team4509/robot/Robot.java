package org.usfirst.frc.team4509.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;

/*
 * Edited by Kyle Brott on 2017-10-26
 * Currently on GitHub @ https://github.com/FRC-4509-MechBulls/PizzaBot
 */

public class Robot extends IterativeRobot {

	public enum EDriveType {
		StartingRight,
		StartingLeft,
		StartingMiddleRight,
		StartingMiddleLeft,
		FPSTest
	}

	/*
	 * CHANGE THIS VALUE TO MATCH WHERE THE ROBOT STARTS| |
	 * Change the value behind the dot,      right here V V
	 * tag:CHANGE_PRE_DEPLOY
	 */
	Robot.EDriveType currentDriveType = EDriveType.StartingRight;
		
	RobotDrive myRobot = new RobotDrive(0, 1);
	Joystick lStick = new Joystick(0); // Left joystick (May not always be the one on the physical left)
	Joystick rStick = new Joystick(1); // Right joystick (May not always be the one on the physical right)
	Timer timer = new Timer();
	CANTalon talonDriveBackLeft   = new CANTalon(5); // The back left driving Talon 
	CANTalon talonDriveBackRight  = new CANTalon(3); // The back right driving Talon
	CANTalon talonDriveFrontLeft  = new CANTalon(8); // The front left driving Talon
	CANTalon talonDriveFrontRight = new CANTalon(0); // The front right driving Talon
	CANTalon talonRopeClimbLeft   = new CANTalon(7);  // The left rope climber Talon
	CANTalon talonRopeClimbRight  = new CANTalon(1); // The right rope climber Talon
	XboxController xControl = new XboxController(0);
	ADXRS450_Gyro gyro = new ADXRS450_Gyro();
	Solenoid solenoid = new Solenoid(4); // The solenoid used to control the pneumatics system
	Compressor compressor = new Compressor(1); // The compressor used to compress air for the pneumatics system
	
	double angle = gyro.getAngle(); // The angle that the robot starts at. Used for straight driving.
	double leftSpeed = 3; // The current speed that the left side of the robot should be driving at. Used for straight driving.
	double rightSpeed = -3; // The current speed that the right side of the robot should be driving at. Negative for forward, positive for backward. Used for straight driving
	boolean isChanged = true; // A boolean that is used in the driving straight method.	

	final double INCHES_ROBOT_TRAVELLED = 23; // Change this value with the amount of inches that the robot traveled during the FPSTest tag:CHANGE_PRE_DEPLOY
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
		
		switch(currentDriveType) {
			case StartingRight:
				autonomousStartingRightSide();
				break;
			case StartingLeft:
				autonomousStartingLeftSide();
				break;
			case StartingMiddleRight:
				autonomousStartingMiddle(true);
				break;
			case StartingMiddleLeft:
				autonomousStartingMiddle(false);
				break;
			case FPSTest:
				fpsTester();
				break;
		}
	}
	
	/**
	 * This function is called periodically during autonomous mode
	 */
	@Override
	public void autonomousPeriodic() {  }
	
	/**
	 * Test feet per second
	 */
	public void fpsTester() {
		while(timer.hasPeriodPassed(timer.get() + 2))
			setDriveTalons(3, -3);
		while(timer.hasPeriodPassed(timer.get() + 0.1))
			setDriveTalons(-1, 1);
	}

	/**
	 * Autonomously go around the airship from the left starting position
	 */
	public void autonomousStartingLeftSide() {
		driveForFeet(7.77083);
		turnForDegrees(45);
		driveForFeet(5);
		driveForFeet(-5);
		turnForDegrees(-45);
		driveForFeet(10);
	}

	/**
	 * Autonomously go around the airship from the right starting position
	 */
	public void autonomousStartingRightSide() {
		driveForFeet(7.77083);
		turnForDegrees(-45);
		driveForFeet(5);
		driveForFeet(-5);
		turnForDegrees(45);
		driveForFeet(10);
	}

	/**
	 * Autonomously go around the airship from the middle starting position
	 *
	 * @param turnRight positive to go around on the right side, negative for
	 *                  the left side
	 */
	public void autonomousStartingMiddle(boolean turnRight) {
		driveForFeet(7.77083);
		driveForFeet(-4);
		if(turnRight) {
			turnForDegrees(90);
			driveForFeet(6);
			turnForDegrees(-45);
			driveForFeet(10);
		} else {
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

		while(isEnabled() && isOperatorControl()){
			SmartDashboard.putString("DB/String 0", "Base speed: " + baseSpeed);
			
			// THROTTLE JOYSTICK
			// throttle wheel is located on right hand joystick
			// The Throttle variant goes from
			// 4 to 8 to 12
			if(rStick.getThrottle() >= -1 && rStick.getThrottle() < -0.25) // lowest 3rd of throttle wheel
				baseSpeed = 4;
			if(rStick.getThrottle() >= -0.25 && rStick.getThrottle() <= 0.5) // middle 3rd of the throttle wheel
				baseSpeed = 8;
			if(rStick.getThrottle() > 0.5 && rStick.getThrottle() <= 1) // top 3rd of throttle wheel
				baseSpeed = 12;
			
			// DRIVE JOYSTICK
			// negative and positive baseSpeed keeps the Drivetrain going the same direction since one has to be inverse
			setDriveTalons(lStick.getY() * -baseSpeed, rStick.getY() * baseSpeed);
			
			// ROPECLIMB JOYSTICK
			if(rStick.getRawButton(1)) { // raw button 1 is the trigger
				setClimbTalons(-4, 4);
				SmartDashboard.putString("DB/String 5","Trigger hit");
			} else
				setClimbTalons(0, 0);

			// Gear Controls
			solenoid.set(lStick.getRawButton(1)); // opens or closes gate
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
	
	/**
	 * Drive for given distance in feet
	 * Has a 1 degree margin of error for driving straight
	 *
	 * @param feet the amount of feet to drive for. a positive value will make it
	 *             go forwards, while a negative will make it go backwards.
	 */
	public void driveForFeet(double feet) {
		angle = gyro.getAngle(); // Set angle to the current angle so that no matter what the original angle was, the robot will drive straight according to the current angle
		double time = timer.get(); // Set time to the current time
		if(feet > 0) { // If feet is positive
			leftSpeed = 3; // Start driving forward
			rightSpeed = 3; 
			while(!timer.hasPeriodPassed(time + getSeconds(feet))) { // If the amount of time needed to travel the number of feet needed hasn't passed
				correctAngle();

				// Set the speeds back to normal. Called when the robot's angle is within the margin of error
				isChanged = true;
				setDriveTalons(3, -3);
				// SmartDashboard.putString("DB/String 5", "Going Straight");
			}
			
			// supply a quick burst of backward motion to stop faster.
			time = timer.get();
			while(!timer.hasPeriodPassed(time + 0.2))
				setDriveTalons(-2, 2);

		} else if(feet < 0) { // If feet is negative
			leftSpeed = -3; // start driving backward
			rightSpeed = -3;
			while(!timer.hasPeriodPassed(time + getSeconds(feet))) { // If the amount of time needed to travel the number of feet needed hasn't passed
				correctAngle();
				
				// Set the speeds back to normal. Called when the robot's angle is within the margin of error
				isChanged = true;
				setDriveTalons(-3, 3);
				// SmartDashboard.putString("DB/String 5", "Going Straight");
			}
			
			// Supply quick burst of forward motion to make the robot stop faster.
			time = timer.get();
			while(!timer.hasPeriodPassed(time + 0.2))
				setDriveTalons(3, -3);

		}

		setDriveTalons(0, 0); // Stop the robot
	}

	/**
	 * Turn the robot to match the universal angle, with a 1° margin of error.
	 */
	public void correctAngle() {
		while(!(gyro.getAngle() - angle > -0.5 && gyro.getAngle() - angle < 0.5)) { // make sure it's still correct

			while(gyro.getAngle() - angle < -0.5) { // If the angle is too much to the left (.5° margin of error allowed)
				if(isChanged) { // Check isChanged. This makes sure that the drive values don't change too drastically.
					// Slow down left side and speed up right
					leftSpeed++;
					rightSpeed++;
					isChanged = false;
				}
				setDriveTalons();
			}

			while(gyro.getAngle() - angle > 0.5) { // If the angle is too far to the right (.5° margin of error allowed)
				// Slow down right side and speed up left
				if(isChanged) { // Check isChanged. This makes sure that the drive values don't change too drastically.
					leftSpeed--;
					rightSpeed--;
					isChanged = false;
				}
				setDriveTalons();
			}

		}
	}

	/**
	 * Sets the drive talons' speed to the universal speeds
	 */
	public void setDriveTalons() {
		talonDriveBackLeft.set(leftSpeed);
		talonDriveFrontLeft.set(leftSpeed);
		talonDriveBackRight.set(rightSpeed);
		talonDriveFrontRight.set(rightSpeed);
	}

	/**
	 * Sets the drive talons' speeds and the universal speeds to the given
	 *
	 * @param left  the speed to set the left talons
	 * @param right the speed to set the right talons
	 */
	public void setDriveTalons(double left, double right) {
		leftSpeed = left;
		rightSpeed = right;
		talonDriveBackLeft.set(left);
		talonDriveFrontLeft.set(left);
		talonDriveBackRight.set(right);
		talonDriveFrontRight.set(right);
	}

	/**
	 * Sets the climb talons' speeds to the given
	 *
	 * @param left  the speed to set the left talon
	 * @param right the speed to set the right talon
	 */
	public void setClimbTalons(double left, double right) {
		talonRopeClimbLeft.set(left);
		talonRopeClimbRight.set(right);
	}
	
	/**
	 * Rotate the robot given degrees, with a 1° margin of error
	 *
	 * @param degreesToTurn the amount of degrees to turn, with positive being
	 *                      right and negative being left.
	 */
	public void turnForDegrees(double degreesToTurn) {
		double startingAngle = gyro.getAngle(); // The angle that the robot starts at
		double targetAngle = gyro.getAngle() + degreesToTurn; // The angle the robot wants to end at
		while(startingAngle + degreesToTurn - 0.5 > gyro.getAngle() || startingAngle + degreesToTurn + 0.5 < gyro.getAngle()) { // if the angle isn't the correct angle
			if(gyro.getAngle() - targetAngle > 0.5) // If the current angle is too far right
				setDriveTalons(-3.5, -3.5);
			else if(gyro.getAngle() - targetAngle < -0.5) // If the current angle is too far left
				setDriveTalons(3.5, 3.5);
		}

		setDriveTalons(0, 0); // Stop the robot
	}

	/**
	 * Get the amount of time the robot will take to travel the given distance
	 *
	 * @param distance the amount of distance that the robot will travel
	 * @return the amount of time the robot will take
	 */
	public double getSeconds(double distance) {
		return Math.abs(distance / FEET_PER_SECOND);
	}

}