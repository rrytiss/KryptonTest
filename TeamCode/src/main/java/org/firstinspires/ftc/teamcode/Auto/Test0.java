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
    double Galia[];/// Variklių galios
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

        odo.resetPosAndIMU();
    }

    @Override
    public void loop() {
        pos = odo.getPosition();
        // Pradinės pozicijos gavimas
        X_Pradinis = pos.getX(DistanceUnit.CM);
        Y_Pradinis = pos.getY(DistanceUnit.CM);
        Z_Pradinis = pos.getHeading(AngleUnit.RADIANS);
        // Tikslų nustatymas
        X_Tikslas = 0;
        Y_Tikslas = 0;
        Z_Tikslas = 0;
        // Atstumo nustatymas
        Xe = X_Tikslas - X_Pradinis;
        Ye = Y_Tikslas - Y_Pradinis;
        Ze = Z_Tikslas - Z_Pradinis;

        Galia[1] = Xe - Ye - Ze;///Kairio priekinio galia
        Galia[2] = Xe + Ye - Ze;///Kairio galinio galia,
        Galia[3] = Xe + Ye + Ze;///Dešinio priekinio galia
        Galia[4] = Xe - Ye + Ze;///Dešinio galino galia

    // Galios normalizavimas
        if (Galia[1] > max){
            Galia[1] = max;
        }
        else {
            Galia[1] = -max;
        }
///================================
        if (Galia[2] > max){
            Galia[2] = max;
        }
        else {
            Galia[2] = -max;
        }
///===============================
        if (Galia[3] > max){
            Galia[3] = max;
        }
        else {
            Galia[3] = -max;
        }
///===============================
        if (Galia[4] > max){
            Galia[4] = max;
        }
        else {
            Galia[4] = -max;
        }
///===============================

        kP.setPower(Galia[1]);
        kG.setPower(Galia[2]);
        dP.setPower(Galia[3]);
        dG.setPower(Galia[4]);

        odo.update();
        /// Atsarginis sustabdymas (Emergency stop)
        if (gamepad1.cross){
            Galia[1] = 0;
            Galia[2] = 0;
            Galia[3] = 0;
            Galia[4] = 0;
        }


        telemetry.addData("X: ", pos.getX(DistanceUnit.CM));
        telemetry.addData("Y: ", pos.getY(DistanceUnit.CM));
        telemetry.addData("Kampas: ", pos.getHeading(AngleUnit.RADIANS));
    }
}
