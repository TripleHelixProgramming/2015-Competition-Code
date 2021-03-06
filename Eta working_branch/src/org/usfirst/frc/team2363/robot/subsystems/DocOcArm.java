package org.usfirst.frc.team2363.robot.subsystems;

import org.usfirst.frc.team2363.robot.commands.grippers.dococ.DocOcManual;
import org.usfirst.frc.team2363.robot.util.ClawPosition;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.ControlMode;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class DocOcArm extends Subsystem {
	
	public static final int ELEVATION_CALIBRATION = 890;//659	//Practice
	//CHANGE ELEVATION BACK TO 534
	public static final int YAW_CALIBRATION = 225;//286

	public enum DocOcArmPosition {
		LEFT_STOWED(ELEVATION_CALIBRATION - 280, YAW_CALIBRATION - 70),
		LEFT_FLOOR(ELEVATION_CALIBRATION, YAW_CALIBRATION),
		LEFT_CLEAR_CAN(ELEVATION_CALIBRATION - 135, YAW_CALIBRATION + 51),
		LEFT_SECOND_CAN(ELEVATION_CALIBRATION - 135, YAW_CALIBRATION + 85),
		LEFT_HANDOFF(ELEVATION_CALIBRATION - 250, YAW_CALIBRATION - 40),
		LEFT_OFF_FLOOR(ELEVATION_CALIBRATION - 225, YAW_CALIBRATION - 0),
		PREP_FOR_HANDOFF(ELEVATION_CALIBRATION - 205, YAW_CALIBRATION),
		LEFT_KNOCK_OVER(ELEVATION_CALIBRATION, YAW_CALIBRATION - 21);
		
		private double elevation;
		private double yaw;

		private DocOcArmPosition(double elevation, double yaw) {
			this.elevation = elevation;
			this.yaw = yaw;
		}

		public double getElevation() {
			return elevation;
		}
		
		public double getYaw() {
			return yaw;
		}
	}

	private DoubleSolenoid claw;
	private CANTalon yaw;
	private CANTalon elevation;


	public DocOcArm(int openClawChannel, int closeClawChannel, int rotateMotorChannel, int extendMotorChannel) {
		claw = new DoubleSolenoid(openClawChannel, closeClawChannel);

		yaw = new CANTalon(rotateMotorChannel);
		yaw.changeControlMode(ControlMode.PercentVbus);
		yaw.setFeedbackDevice(CANTalon.FeedbackDevice.AnalogPot);
//		yaw.setPID(0, 0.04, 0);
		yaw.setPID(0.0001, 0, 0);
		yaw.reverseOutput(true);
		yaw.setForwardSoftLimit((int)600); //DocOcArmPosition.LEFT_HANDOFF.getElevation()); 
		yaw.enableForwardSoftLimit(true);
		yaw.setReverseSoftLimit((int)100); //DocOcArmPosition.LEFT_STOWED.getElevation()); 
		yaw.enableReverseSoftLimit(true);

		elevation = new CANTalon(extendMotorChannel);
		elevation.changeControlMode(ControlMode.PercentVbus);
		elevation.setFeedbackDevice(CANTalon.FeedbackDevice.AnalogPot);
		elevation.setPID(0, 0.05, 0);
		elevation.reverseOutput(true);
		elevation.setForwardSoftLimit((int)DocOcArmPosition.LEFT_FLOOR.getElevation());
		elevation.enableForwardSoftLimit(true);
		elevation.setReverseSoftLimit((int)DocOcArmPosition.LEFT_STOWED.getElevation());
		elevation.enableReverseSoftLimit(true);
	}

	@Override 
	public void initDefaultCommand() {
		setDefaultCommand(new DocOcManual());
	}
	
	public void setYawControlMethod(ControlMode mode) {
		yaw.changeControlMode(ControlMode.PercentVbus);
	}
	
	public void setElevationControlMethod(ControlMode mode) {
		elevation.changeControlMode(ControlMode.PercentVbus);
	}
	
	public void setControlEnabled(boolean enable) {
		if (enable) {
			elevation.enableControl();
			yaw.enableControl();
		} else {
			elevation.disableControl();
			yaw.disableControl();
		}
	}

	public void setYaw(double speed) {
		if (speed > 0 && getYawPosition() > 344
				|| speed < 0 && getYawPosition() < DocOcArmPosition.LEFT_HANDOFF.getYaw() - 10) {
			yaw.set(0);
		} else {
			yaw.set(speed);
		}
	}

	public void setElevation(double position) {
		elevation.set(position);
	}

	public void setClaw(ClawPosition position) {
		if (position == ClawPosition.OPEN) {
			claw.set(DoubleSolenoid.Value.kReverse);
		} else {
			claw.set(DoubleSolenoid.Value.kForward);
		}
	}
	
	public boolean isElevationOnTarget(DocOcArmPosition position) {
		return Math.abs(elevation.getPosition() - position.getElevation()) < 5;
	}
	
	public boolean isYawOnTarget(DocOcArmPosition position) {
		return Math.abs(yaw.getPosition() - position.getYaw()) < 1;
	}
	
	public double getYawPosition() {
		SmartDashboard.putNumber("Yaw Voltage", yaw.getAnalogInRaw());
		return yaw.getPosition();
	}
	
	public double getElevationPosition() {
		return elevation.getPosition();
	}
	
	public double getYawSpeed() {
		return yaw.getSpeed();
	}
	
	public double getElevationSpeed() {
		return elevation.getSpeed();
	}
	
	public double getYawCurrent() {
		return yaw.getOutputCurrent();
	}
	
	public double getElevationCurrent() {
		return elevation.getOutputCurrent();
	}
	
	public boolean hasCan() {
		return !elevation.isRevLimitSwitchClosed();
	}
}


