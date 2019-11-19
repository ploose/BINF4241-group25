import java.util.Scanner;

class WashingMachine implements IWashingMachine {
    private boolean isOn, isRunning;
    private int time, temperature;
    private Programs.Program program;
    private TimerThread timer;

    private static WashingMachine uniqueInstance;

    private WashingMachine() {
        isOn = false;
        isRunning = false;
        program = null;
    }

    static WashingMachine getUniqueInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new WashingMachine();
        }

        return uniqueInstance;
    }

    public void switchOn() {
        if (isOn) {
            System.out.println("Washing machine is already on.");
        } else {
            isOn = true;
        }
    }

    public void switchOff() {
        if (!isOn) {
            System.out.println("Dishwasher is already off.");
        } else if (isRunning) {
            System.out.println("Cannot turn the washing machine off, it is still running.");
        } else {
            isOn = false;
        }
    }

    private void setTemperature(int temperature) {
        if (temperature > 20 && temperature < 100) {
            this.temperature = temperature;
        } else {
            System.out.println("This temperature is too hot or too low.");
        }
    }

    private int getTimer() {
        if (!isOn) {
            System.out.println("The washing machine is off.");
            return -1;
        }

        if (program == null) {
            System.out.println("You first have to choose a program.");
            return -2;
        }

        if (isRunning) {
            return timer.getTime();
        } else {
            return time;
        }
    }

    private void chooseProgram(Scanner input) {
        System.out.println("You can choose between the following programs:");
        System.out.print("-double rinse \n -intense \n -quick \n -spin");

        String decision = input.next();

        switch (decision) {
            case "double rinse":
                program = Programs.Program.rinse;
                time = 5;

            case "intense":
                program = Programs.Program.intense;
                time = 6;

            case "quick":
                program = Programs.Program.quick;
                time = 7;

            case "spin":
                program = Programs.Program.spin;
                time = 8;

            default:
                System.out.println("Wrong input.");
                chooseProgram(input);
        }
    }

    private void start() {
        if (!isOn) {
            System.out.println("You first need to start the washing machine.");
        }

        if (program == null) {
            System.out.println("You first need to choose a program");
        }

        if (temperature == -1) {
            System.out.println("You need to enter a temperature");
        }

        if (isRunning) {
            System.out.println("The washing machine  has already started.");
        }

        timer = new TimerThread(time);
        Thread runner = new Thread(timer);

        isRunning = true;
        runner.start();

        for (;;) {
            if (!timer.isRunning()) {
                break;
            }
        }

        time = 0;
        timer = null;
        program = null;
        isRunning = false;
        temperature = -1;
    }

    public void execute() {
        if (!isOn) {
            System.out.println("The device is turned off.");
        }

        System.out.println("You can choose following functions: ");
        System.out.print("-set temperature (1) \n -get timer (2) \n -choose program (3) \n -start (4)");

        Scanner input = new Scanner(System.in);
        String decision = input.next();

        switch (decision) {
            case "1":
                System.out.print("Choose a temperature: ");
                setTemperature(input.nextInt());

            case "2":
                int duration = getTimer();

                if (duration >= 0) {
                    System.out.println("The device needs " + duration + "s to complete the action.");
                }

            case "3":
                chooseProgram(input);

            case "4":
                start();

            default:
                System.out.println("Wrong Input");
                execute();
        }
    }
}