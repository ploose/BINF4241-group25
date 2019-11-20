import java.util.Scanner;

public class Oven implements IOven{

    private boolean turnedOn;
    private int temperature;
    private Program program;
    private boolean cooking;
    private TimerThread timer;
    private Scanner input;
    int time;
    private Smartphone huawei;


    private static Oven uniqueInstance;

    private Oven(Smartphone huawei){
        program = null;
        temperature = 0;
        turnedOn = false;
        cooking = false;
        timer = new TimerThread(0);
        input = new Scanner(System.in);
        this.huawei = huawei;
    }

    static Oven getUniqueInstance(Smartphone huawei) {
        if (uniqueInstance == null) {
            uniqueInstance = new Oven(huawei);
        }

        return uniqueInstance;
    }

    public boolean switchOn(){
        if (turnedOn) {
            return false;
        }

        else {
            turnedOn = true;
            return true;
        }
    }

    public void setTimer(int timeInSeconds){
        timer = new TimerThread(timeInSeconds);

    }

    public void setTemperature(int temperature){
        this.temperature = temperature;
    }

    public void setProgram(){   //TODO
        System.out.println("You can choose between the following programs:");
        System.out.print("-ventilated\n -grill\n-reheat\n");

        String decision = input.next();

        switch (decision) {
            case "ventilated":
                program = Program.ventilated;
                time = 5;
                timer.setTimer(time);
                break;

            case "grill":
                program = Program.grill;
                time = 6;
                timer.setTimer(time);
                break;

            case "reheat":
                program = Program.reheat;
                time = 7;
                timer.setTimer(time);
                break;

            default:
                System.out.println("Wrong input.");
                setProgram();
                break;
        }

    }

    public void startCooking(){

        if (timer.getTime() == 0) {
            System.out.println("Set the timer first. \n");
            execute();
        }
        else if (!turnedOn) {
            System.out.println("The oven is off. \n");
            execute();
        }
        else if (temperature == 0){
            System.out.println("There is no temperature set.");
        }
        cooking = true;
        Thread runner = new Thread(timer);
        runner.start();
        cooking = false;
    }

    public int checkTimer(){
        return timer.getTime();
    }

    public void interruptProgram() {
        if (cooking && turnedOn == true) {
            cooking = false;
            program = null;
        }
    }

    public boolean switchOff(){
        turnedOn = false;
        return true;
    }

    public void execute() {
        if (!turnedOn) {
            System.out.println("The device is turned off.");
        }

        else {
            System.out.println("You can choose following functions: ");
            System.out.print("-set temperature (1) \n -get timer (2) \n -choose program (3) \n");
            System.out.print("-start (4) \n -exit (5) \n");

            String decision = input.next();

            switch (decision) {
                case "1":
                    System.out.print("Choose a temperature: ");
                    setTemperature(input.nextInt());
                    break;
                case "2":
                    int duration = checkTimer();

                    if (duration > 0) {
                        System.out.println("The device needs " + duration + "s to complete the action.");
                    }
                    else{
                        System.out.println("The timer is not set.");
                    }
                    break;

                case "3":
                    setProgram();
                    break;
                case "4":
                    startCooking();
                    break;
                case "5":
                    System.out.println("Returning to main menu.");
                    huawei.mainPage();
                    break;
                default:
                    System.out.println("Wrong Input");
                    execute();
                    break;
            }
        }
    }

    public String toString() {
        if (timer.isRunning()) {
            return "The oven is on and running.";
        }

        else {
            return "The oven is on.";
        }
    }
}
