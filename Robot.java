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

/*
 * Edited By Brendan Cottenham on 10/13/17 
 * All code has been commented and documented. I renamed BaseSpd to baseSpeed in order to preserve clarity and a naming convention.
 * I also made feetPerSeconds a final double, so it can't be changed within the code at runtime.
 * I also edited the driveForFeet method, so that the quick burst of opposite motion actually happens.
 * As of right now, this is a little violent, but it does work. I'll be working to make this a little more graceful.
 * I renamed the Talons to a better naming convention. Now, they are named Talon<What function they have><Front or Back><Right or Left>. 
 * This makes them much easier to understand.
 */


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot 
{
	RobotDrive myRobot = new RobotDrive(0, 1);
	Joystick LStick = new Joystick(0); // Left joystick (May not always be the one on the physical left)
	Joystick RStick = new Joystick(1); // Right joystick (May not always be the one on the physical right)
	Timer timer = new Timer();
	CANTalon TalonDriveFrontRight = new CANTalon(0); // The front right driving Talon
	CANTalon TalonDriveBackRight = new CANTalon(3); // The back right driving Talon
	CANTalon TalonDriveFrontLeft = new CANTalon(8); // The front left driving Talon
	CANTalon TalonDriveBackLeft = new CANTalon(5); // The back left driving Talon 
	CANTalon TalonRopeClimbRight = new CANTalon(1); // The right rope climber Talon
	CANTalon TalonRopeClimbLeft = new CANTalon(7);  // The left rope climber Talon
	XboxController XControl = new XboxController(0);
	ADXRS450_Gyro gyro = new ADXRS450_Gyro();
	
	double angle = gyro.getAngle(); // The angle that the robot starts at. Used for straight driving.
	double leftSpeed = 3; // The current speed that the left side of the robot should be driving at. Used for straight driving.
	double rightSpeed = -3; // The current speed that the right side of the robot should be driving at. Negative for forward, positive for backward. Used for straight driving
	boolean isChanged = true; // A boolean that is used in the driving straight method.
	
	java.lang.String inputString; // Declare input string
	
	
	final double feetPerSeconds = 23/12; // The speed in feet per second that the robot can move
	
	double baseSpeed = 4;

	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() 
	{
		// Change all of the Talons into voltage control mode
		TalonDriveBackLeft.changeControlMode(CANTalon.TalonControlMode.Voltage);
		TalonDriveBackRight.changeControlMode(CANTalon.TalonControlMode.Voltage);
		TalonDriveFrontLeft.changeControlMode(CANTalon.TalonControlMode.Voltage);
		TalonDriveFrontRight.changeControlMode(CANTalon.TalonControlMode.Voltage);
		TalonRopeClimbRight.changeControlMode(CANTalon.TalonControlMode.Voltage);
		TalonRopeClimbLeft.changeControlMode(CANTalon.TalonControlMode.Voltage);
	}

	/**
	 * This function is run once each time the robot enters autonomous mode
	 */
	@Override
	public void autonomousInit() 
	{
		timer.start();
		timer.reset(); // Make sure the timer is at 0
		inputString = SmartDashboard.getString("DB/String 0", "No Input"); // Get the input from the smart dashboard with a default value of "No Input"
		gyro.calibrate(); //resets gyro position and sets new position as pos. 0
		angle = gyro.getAngle();
		SmartDashboard.putString("DB/String 5", "Timer Started");
		
		autonomousDriveInputReader();
	}
	
	
	// Call the correct method based on the input
	void autonomousDriveInputReader()
	{
		if(inputString.equals("No Input"))
		{
			SmartDashboard.putString("DB/String 5", "No autonomous input detected");
		}
		else if(inputString.toLowerCase().equals("right"))
		{
			SmartDashboard.putString("DB/String 5", "Calling Method autonomousStartingRightSide()");
			autonomousStartingRightSide();
		}
		else if(inputString.toLowerCase().equals("left"))
		{
			SmartDashboard.putString("DB/String 5", "Calling Method autonomousStartingLeftSide()");
			autonomousStartingLeftSide();
		}
		else if(inputString.toLowerCase().equals("middleright"))
		{
			SmartDashboard.putString("DB/String 5", "Calling Method autonomousStartingMiddle(true)");
			autonomousStartingMiddle(true);
		}
		else if(inputString.toLowerCase().equals("middleleft"))
		{
			SmartDashboard.putString("DB/String 5", "Calling Method autonomousStartingMiddle(false)");
			autonomousStartingMiddle(false);
		}
		else
		{
			SmartDashboard.putString("DB/String 5", "Invalid input");
		}
		
		timer.stop();
		//SmartDashboard.putString("DB/String 5", "Timer ended");
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		
	}
	
	// Called when the robot starts on the left
	void autonomousStartingLeftSide()
	{
		driveForFeet(8);
		turnForDegrees(45);
		driveForFeet(5);
		driveForFeet(-5);
		turnForDegrees(-45);
		driveForFeet(10);
	}
	
	// Called when the robot starts on the right
	void autonomousStartingRightSide()
	{
		driveForFeet(8);
		turnForDegrees(-45);
		driveForFeet(5);
		driveForFeet(-5);
		turnForDegrees(45);
		driveForFeet(10);
	}
	
	// Called when the robot starts in the middle. Pass in true to go right around the airship, and false for the left side.
	void autonomousStartingMiddle(boolean turnRight)
	{
		driveForFeet(8);
		driveForFeet(-4);
		if(turnRight)
		{
			turnForDegrees(90);
			driveForFeet(6);
			turnForDegrees(-45);
			driveForFeet(10);
		}
		else
		{
			turnForDegrees(-90);
			driveForFeet(6);
			turnForDegrees(45);
			driveForFeet(10);
		}
		
	}
	
	
	/**
	 * This function is called once each time the robot enters tele-operated
	 * mode
	 */
	@Override
	public void teleopInit() {
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
//		myRobot.tankDrive(LStick.getY(), RStick.getY());

		
		while (isEnabled() && isOperatorControl()){
			SmartDashboard.putString("DB/String 0", "Base speed: " + baseSpeed);
			
			//THROTTLE JOYSTICK
			//throttle wheel is located on right hand joystick
			//The Throttle variant goes from
			//4 to 8 to 12
			if (RStick.getThrottle() <= -0.25 && RStick.getThrottle() >= -1){// lowest 3rd of throttle wheel
				baseSpeed = 4;
			}
			if (RStick.getThrottle() <= 0.5 && RStick.getThrottle() > -0.25){// middle 3rd of the throttle wheel
				baseSpeed = 8;
			}
			if (RStick.getThrottle() <= 1  && RStick.getThrottle() > 0.5){//top 3rd of throttle wheel
				baseSpeed = 12;
			}
			
			//DRIVE JOYSTICK
			// negative and positive BaseSpd keeps the Drivetrain going the same direction since one has to be inverse
			TalonDriveBackLeft.set(LStick.getY()*-baseSpeed);
			TalonDriveFrontLeft.set(LStick.getY()*-baseSpeed);
			TalonDriveFrontRight.set(RStick.getY()*baseSpeed);
			TalonDriveBackRight.set(RStick.getY()*baseSpeed);
			
			//ROPECLIMB JOYSTICK
			if (RStick.getRawButton(1)){// raw button 1 is the trigger
				TalonRopeClimbLeft.set(-4);
				TalonRopeClimbRight.set(4);
				SmartDashboard.putString("DB/String 5","Trigger hit");
			}
			else
			{
				TalonRopeClimbLeft.set(0);
				TalonRopeClimbRight.set(0);
			}
			
			//THROTTLE XBOX
			//The Throttle variant goes from 
			//4 to 8 to 12
			if (baseSpeed < 12){ //restricts the user from going over a drivable speed
			if(XControl.getBumper(Hand.kRight)){ //retrieves the right bumper as a boolean
				baseSpeed += 4;
				if (baseSpeed > 12){ 
					baseSpeed = 12;//sets the speed to max drivable speed when bumper is clicked to increase from the 2nd speed variant
				}
				SmartDashboard.putString("DB/String 5", "speed increase");

			}
			}
			if (baseSpeed > 4){//restricts the user from setting the speed too low
			if(XControl.getBumper(Hand.kLeft)){
				baseSpeed -= 4;
				SmartDashboard.putString("DB/String 5", "speed decrease");

			}
			}
					
			//DRIVE XBOX
			// negative and positive BaseSpd keeps the Drivetrain going the same direction since one has to be inverse
			
			TalonDriveBackLeft.set(XControl.getY(Hand.kLeft)*-baseSpeed); //getY left hand is left joystick y variable
			TalonDriveFrontLeft.set(XControl.getY(Hand.kLeft)*-baseSpeed);
			TalonDriveFrontRight.set(XControl.getRawAxis(5)*baseSpeed);//getY right hand is right joystick y variable
			TalonDriveBackRight.set(XControl.getRawAxis(5)*baseSpeed);
			
			//ROPECLIMB XBOX
			//RopeClimb trigger is right trigger
			while(XControl.getTrigger(Hand.kRight)){
				TalonRopeClimbLeft.set(-4);
				TalonRopeClimbRight.set(4);
			}
		
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
	void driveForFeet(double feet)
	{
		angle = gyro.getAngle(); // Set angle to the current angle so that no matter what the original angle was, the robot will drive straight according to the current angle
		double time = timer.get(); // Set time to the current time
		if(feet > 0) // If feet is positive
		{
			leftSpeed = 3; // Start driving forward
			rightSpeed = 3; 
			while(!timer.hasPeriodPassed(time+GetSeconds(feet))) // If the amount of time needed to travel the number of feet needed hasn't passed
			{
				while(gyro.getAngle() - angle < -.5d) // If the angle is too much to the left (.5 degree margin of error allowed)
				{
					if(isChanged) // Check isChanged. This makes sure that the drive values don't change too drastically.
					{
						leftSpeed += 1;
						rightSpeed += 1;
						isChanged = false;
					}
					
					// Set the talons' speed based on the corrective values
					TalonDriveBackLeft.set(leftSpeed);
					TalonDriveFrontLeft.set(leftSpeed);
					TalonDriveBackRight.set(rightSpeed);
					TalonDriveFrontRight.set(rightSpeed);
				}
				while(gyro.getAngle() - angle > .5d) // If the angle is too far to the right (.5 degree margin of error allowed)
				{
					//Slow down right side and speed up left
					if(isChanged) // Check isChanged. This makes sure that the drive values don't change too drastically.
					{
						leftSpeed -= 1;
						rightSpeed-= 1;
						isChanged = false;
					}
					
					// Set the talons' speed based on the corrective values
					TalonDriveBackLeft.set(leftSpeed);
					TalonDriveFrontLeft.set(leftSpeed);
					TalonDriveBackRight.set(rightSpeed);
					TalonDriveFrontRight.set(rightSpeed);
	
				}
				// Set the speeds back to normal. Called when the robot's angle is within the margin of error
				leftSpeed = 3;
				rightSpeed = -3;
				isChanged = true;
				TalonDriveBackLeft.set(leftSpeed);
				TalonDriveFrontLeft.set(leftSpeed);
				TalonDriveBackRight.set(rightSpeed);
				TalonDriveFrontRight.set(rightSpeed);
				//SmartDashboard.putString("DB/String 5", "Going Straight");
			}
			
			// supply a quick burst of backward motion to stop faster.
			time = timer.get();
			while(!timer.hasPeriodPassed(time + .2d))
			{
				TalonDriveBackLeft.set(-2);
				TalonDriveFrontLeft.set(-2);
				TalonDriveFrontRight.set(2);
				TalonDriveBackRight.set(2);
			}
		}
		else if(feet < 0) // If feet is negative
		{
			leftSpeed = -3; // start driving backward
			rightSpeed = -3;
			while(!timer.hasPeriodPassed(time + GetSeconds(feet))) // If the amount of time needed to travel the number of feet needed hasn't passed
			{
				while(gyro.getAngle() - angle < -.5d) // If the angle is too much to the left (.5 degree margin of error allowed)
				{
					if(isChanged) // Check isChanged. This makes sure that the drive values don't change too drastically.
					{
						leftSpeed += 1;
						rightSpeed += 1;
						isChanged = false;
					}
					TalonDriveBackLeft.set(leftSpeed);
					TalonDriveFrontLeft.set(leftSpeed);
					TalonDriveBackRight.set(rightSpeed);
					TalonDriveFrontRight.set(rightSpeed);
				}
				while(gyro.getAngle() - angle > .5d) // If the angle is too much to the right (.5 degree margin of error allowed)
				{
					if(isChanged) // Check isChanged. This makes sure that the drive values don't change too drastically.
					{
						leftSpeed -= 1;
						rightSpeed -= 1;
						isChanged = false;
					}
					TalonDriveBackLeft.set(leftSpeed);
					TalonDriveFrontLeft.set(leftSpeed);
					TalonDriveBackRight.set(rightSpeed);
					TalonDriveFrontRight.set(rightSpeed);
	
				}
				
				// Set the speeds back to normal. Called when the robot's angle is within the margin of error
				leftSpeed = -3;
				rightSpeed = 3;
				isChanged = true;
				TalonDriveBackLeft.set(leftSpeed);
				TalonDriveFrontLeft.set(leftSpeed);
				TalonDriveBackRight.set(rightSpeed);
				TalonDriveFrontRight.set(rightSpeed);
				//SmartDashboard.putString("DB/String 5", "Going Straight");
			}
			
			// Supply quick burst of forward motion to make the robot stop faster.
			time = timer.get();
			while(!timer.hasPeriodPassed(time + .2d))
			{
				TalonDriveBackLeft.set(3);
				TalonDriveFrontLeft.set(3);
				TalonDriveFrontRight.set(-3);
				TalonDriveBackRight.set(-3);
			}
		}
		
		// Stop the robot
		TalonDriveBackLeft.set(0);
		TalonDriveFrontLeft.set(0);
		TalonDriveBackRight.set(0);
		TalonDriveFrontRight.set(0);
	}
	
	/*
	 * This method will make the robot turn x amount of degrees. The parameter can accept negative numbers.
	 * Positive numbers make the robot turn to the right, and negative numbers make it turn to the left.
	 * Has a 1 degree margin of error
	 */
	void turnForDegrees(double degreesToTurn)
	{
		double startingAngle = gyro.getAngle(); // The angle that the robot starts at
		double targetAngle = gyro.getAngle() + degreesToTurn; // The angle the robot wants to end at
		while(startingAngle + degreesToTurn -.5d > gyro.getAngle() || startingAngle + degreesToTurn + .5d < gyro.getAngle()) // if the angle isn't the correct angle
		{
			if(gyro.getAngle() - targetAngle > .5f) // If the current angle is too far right
			{
				TalonDriveBackLeft.set(-3.5d);
				TalonDriveFrontLeft.set(-3.5d);
				TalonDriveBackRight.set(-3.5d);
				TalonDriveFrontRight.set(-3.5d);
			}
			else if(gyro.getAngle() - targetAngle < -.5f) // If the current angle is too far left
			{
				TalonDriveBackLeft.set(3.5d);
				TalonDriveFrontLeft.set(3.5d);
				TalonDriveBackRight.set(3.5d);
				TalonDriveFrontRight.set(3.5d);
			}
		}
		
		// Stop the robot
		TalonDriveBackLeft.set(0);
		TalonDriveFrontLeft.set(0);
		TalonDriveBackRight.set(0);
		TalonDriveFrontRight.set(0);
	}
	
	// Returns the amount of seconds needed in order to travel the distance that's passed in
	double GetSeconds(double distance)
	{
		return Math.abs(distance/feetPerSeconds);
	}
}