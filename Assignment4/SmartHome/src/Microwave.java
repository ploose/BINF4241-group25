import java.util.Scanner;

public class Microwave implements IMicrowave {
    private boolean turnedOn, baking;
    private int temperature;
    private TimerThread timer;
    private Scanner input;
    private static Microwave uniqueInstance;

    private Microwave(){
        temperature = 0;
        turnedOn = false;
        baking = false;
        timer = new TimerThread(3);
        input = new Scanner(System.in);
    }

    static Microwave getUniqueInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new Microwave();
        }

        return uniqueInstance;
    }

    @Override
    public boolean switchOn(){
        turnedOn = true;
        System.out.println("Switching on");
        return true;
    }

    @Override
    public void setTimer(int timeInSeconds){
        timer = new TimerThread(timeInSeconds);
        timer.run();
    }

    @Override
    public void setTemperature(int temperature){
        this.temperature = temperature;
    }

    @Override
    public void startBaking(){
        if (temperature != 0 && turnedOn){
            baking = true;
        }
    }

    @Override
    public int checkTimer(){
        return timer.getTime();
    }

    @Override
    public void interruptProgram() {
        if (baking && turnedOn) {
            baking = false;
        }
    }

    @Override
    public boolean switchOff(){
        turnedOn = false;
        return true;
    }

    @Override
    public void execute() {
        if (!turnedOn) {
            System.out.println("The device is turned off.");
        }

        else {
            System.out.println("You can choose following functions: ");
            System.out.print("-set temperature (1) \n -get timer (2) \n -start(3) \n");
            System.out.print("-exit (4) \n");

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
                    break;

                case "3":
                    startBaking();
                    break;

                case "4":
                    System.out.println("Returning to main menu.");
                    break;

                default:
                    System.out.println("Wrong Input");
                    execute();
                    break;
            }
        }
    }

    }
