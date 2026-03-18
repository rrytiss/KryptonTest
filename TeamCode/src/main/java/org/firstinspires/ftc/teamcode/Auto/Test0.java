package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.GoBildaPinpointDriver;

@TeleOp
public class Test0 extends OpMode {
    double max = 0.4;

    GoBildaPinpointDriver odo;
    DcMotor kP, dG, dP, kG;
    double Galia_kP, Galia_dP, Galia_kG, Galia_dG;/// Variklių galios
    Pose2D pos;
    double X_Pradinis, Y_Pradinis, Z_Pradinis;///X = pradinis x; Y = pradinis y (strafe); Z = pradinis kampas
    double X_Tikslas, Y_Tikslas, Z_Tikslas;///Taikinio X, Y, Z (target error)
     double Xe, Ye, Ze; /// Tikslo error (Kiek reikia iki tikslo)
    @Override
    public void init() {
        odo = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        kP = hardwareMap.get(DcMotor.class, "kP");
        dP = hardwareMap.get(DcMotor.class, "dP");
        kG = hardwareMap.get(DcMotor.class, "kG");
        dG = hardwareMap.get(DcMotor.class, "dG");

        kP.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        kG.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        dP.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        dG.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        odo.setOffsets(-40.0, -151.0, DistanceUnit.MM);
        odo.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        odo.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);

        odo.resetPosAndIMU();
        Pose2D startPos = new Pose2D(DistanceUnit.MM, -8, -9, AngleUnit.RADIANS, 0);
        odo.setPosition(startPos);

        pos = odo.getPosition();
        telemetry.addData("X: ", pos.getX(DistanceUnit.CM));
        telemetry.addData("Y: ", pos.getY(DistanceUnit.CM));
        telemetry.addData("Kampas: ", pos.getHeading(AngleUnit.RADIANS));

    }

    @Override
    public void loop() {
        pos = odo.getPosition();
        // Pradinės pozicijos gavimas
        X_Pradinis = pos.getX(DistanceUnit.CM);
        Y_Pradinis = pos.getY(DistanceUnit.CM);
        Z_Pradinis = pos.getHeading(AngleUnit.RADIANS);
        // Tikslų nustatymas
        X_Tikslas = 20;
        Y_Tikslas = 0;
        Z_Tikslas = 0;
        // Atstumo nustatymas
        Xe = X_Tikslas - X_Pradinis;
        Ye = Y_Tikslas - Y_Pradinis;
        Ze = Z_Tikslas - Z_Pradinis;

        Galia_kP = Xe - Ye - Ze;///Kairio priekinio galia
        Galia_kG = Xe + Ye - Ze;///Kairio galinio galia,
        Galia_dP = Xe + Ye + Ze;///Dešinio priekinio galia
        Galia_dG = Xe - Ye + Ze;///Dešinio galino galia

    // Galios normalizavimas
        if (Galia_kP > max){
            Galia_kP = max;
        }
        else {
            Galia_kP = -max;
        }
///================================
        if (Galia_kG> max){
            Galia_kG = max;
        }
        else {
            Galia_kG = -max;
        }
///===============================
        if (Galia_dP > max){
            Galia_dP = max;
        }
        else {
            Galia_dP = -max;
        }
///===============================
        if (Galia_dG > max){
            Galia_dG = max;
        }
        else {
            Galia_dG = -max;
        }
///===============================

        kP.setPower(Galia_kP);
        kG.setPower(Galia_kG);
        dP.setPower(Galia_dP);
        dG.setPower(Galia_dG);

        odo.update();
        /// Atsarginis sustabdymas (Emergency stop)
        if (gamepad1.cross){
            Galia_kP = 0;
            Galia_kG = 0;
            Galia_dG = 0;
            Galia_dP = 0;
        }
        if(Xe < 1 && Ze <1  && Ye < 1 && Xe > -1 && Ye > -1 && Ze > -1) {
            Galia_kP = 0;
            Galia_kG = 0;
            Galia_dG = 0;
            Galia_dP = 0;
        }


        telemetry.addData("X: ", pos.getX(DistanceUnit.CM));
        telemetry.addData("Y: ", pos.getY(DistanceUnit.CM));
        telemetry.addData("Kampas: ", pos.getHeading(AngleUnit.RADIANS));
    }
}
