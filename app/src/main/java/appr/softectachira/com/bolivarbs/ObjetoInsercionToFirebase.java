package appr.softectachira.com.bolivarbs;


/**
 * Created by CASA on 17/12/2017.
 */

public class ObjetoInsercionToFirebase {

    public String nombre;
    public double money;
    public  int partidasJugadas;
    public double money_pendiente;

    public ObjetoInsercionToFirebase(String nombre, double money, int partidasJugadas,double money_pendiente) {
        this.nombre = nombre;
        this.money = money;
        this.partidasJugadas=partidasJugadas;
        this.money_pendiente=money_pendiente;

    }

    public ObjetoInsercionToFirebase(double money) {
        this.money = money;
    }


}
