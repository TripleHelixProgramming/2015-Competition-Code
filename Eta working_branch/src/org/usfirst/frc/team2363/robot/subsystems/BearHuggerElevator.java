package org.usfirst.frc.team2363.robot.subsystems;

import static org.usfirst.frc.team2363.robot.RobotMap.*;

import org.usfirst.frc.team2363.robot.commands.elevators.bearhugger.BearHuggerManually;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.PIDSource.PIDSourceParameter;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
@SuppressWarnings("unused")
public class BearHuggerElevator extends Subsystem {
	
	public static final double CAN_HAND_OFF = 3;
	public static final double BEAR_HUGGER_ELEVATOR_DISTANCE_PER_PULSE = 0.030;
	public static final double BEAR_HUGGER_ELEVATOR_TOP_LIMIT = 30;
	public static final double BEAR_HUGGER_ELEVATOR_POWER = 1;
    
    private SpeedController elevator = new Talon(BEAR_HUGGER_ELEVATOR_TALON_CHANNEL);
    private static Encoder encoder = new Encoder(BEAR_HUGGER_ELEVATOR_CHANNEL_A, BEAR_HUGGER_ELEVATOR_CHANNEL_B, true, EncodingType.k4X);

    public BearHuggerElevator() {
    	encoder.setDistancePerPulse(BEAR_HUGGER_ELEVATOR_DISTANCE_PER_PULSE);
		encoder.setPIDSourceParameter(PIDSourceParameter.kDistance);
		
		encoder.setMaxPeriod(0.1);
    }
    
    public void initDefaultCommand() {
    }
    
    public void drive(double power) {
    	if (isAtTopLimit() && power > 0) {
    		elevator.set(0);
    	} else {
    		elevator.set(power);
    	}
    	System.out.println("Bear Hugger Elevator Power: " + power);
    }
    
    public double getPosition() {
		return encoder.getDistance();
	}
    
    public boolean isAtTopLimit() {
    	return encoder.getDistance() > BEAR_HUGGER_ELEVATOR_TOP_LIMIT;
    }
    
    public boolean isAtClearLimit() {
    	return encoder.getDistance() > BEAR_HUGGER_ELEVATOR_TOP_LIMIT - 3;
    }

	public boolean isBetweenAdjustHeights() {
		return encoder.getDistance() > 1 && encoder.getDistance() < 4;
	}

	public boolean isElevatorMoving() {
		return !encoder.getStopped();
	}

	public void resetAtBottom() {
		encoder.reset();
	}
}

