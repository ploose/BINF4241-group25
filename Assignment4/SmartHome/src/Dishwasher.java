import java.util.Scanner;

class Dishwasher implements IDishwasher, Command {
    private boolean isOn;
    private int time;
    private Program program;
    private TimerThread timer;
    private Scanner input;

    private static Dishwasher uniqueInstance;

    private Dishwasher() {
        isOn = false;
        program = null;

        input = new Scanner(System.in);
        timer = new TimerThread(0);
    }

    static Dishwasher getUniqueInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new Dishwasher();
        }

        return uniqueInstance;
    }

    public void switchOn() {
        if (isOn) {
            System.out.println("Dishwasher is already on.");
        }

        else {
            isOn = true;
        }
    }

    public void switchOff() {
        if (!isOn) {
            System.out.println("Dishwasher is already off.");
        }

        else if (timer.isRunning()) {
            System.out.println("Cannot turn the washing machine off, it is still running.");
        }

        else {
            isOn = false;
        }
    }

    private int getTimer() {
        if (!isOn) {
            System.out.println("The dishwasher is off.");
            return -1;
        }

        if (program == null) {
            System.out.println("You first have to choose a program.");
            return -2;
        }

        if (timer.isRunning()) {
            return timer.getTime();
        }

        else {
            return time;
        }
    }

    private void chooseProgram() {
        System.out.println("You can choose between the following programs:");
        System.out.print("-glasses \n -plates \n -pans \n -mixed");

        String decision = input.next();

        switch (decision) {
            case "glasses":
                program = Program.glasses;
                timer.setTimer(5);

            case "plates":
                program = Program.plates;
                timer.setTimer(6);

            case "pans":
                program = Program.pans;
                timer.setTimer(7);

            case "mixed":
                program = Program.mixed;
                timer.setTimer(8);

            default:
                System.out.println("Wrong input. \n");
                chooseProgram();
        }
    }

    private void start() {
        if (!isOn) {
            System.out.println("You first need to start the dishwasher.");
        }

        if (program == null) {
            System.out.println("You first need to choose a program");
        }

        if (timer.isRunning()) {
            System.out.println("The dishwasher has already started.");
        }

        Thread runner = new Thread(timer);
        runner.start();

        time = 0;
    }

    private void stop() {
        if (!isOn) {
            System.out.println("The dishwasher is not on.");
        }

        if (!timer.isRunning()) {
            System.out.println("The dishwasher is not running.");
        }

        time = 0;
        timer = null;
        program = null;
    }

    public void execute() {
        if (!isOn) {
            System.out.println("The device is turned off.");
        }

        else {
            System.out.println("You can choose following functions: ");
            System.out.print("-get timer (1) \n -choose program (2) \n -start (3) \n");
            System.out.print("-stop (4) \n -exit (5)");

            String decision = input.next();

            switch (decision) {
                case "1":
                    int duration = getTimer();

                    if (duration >= 0) {
                        System.out.println("The device needs " + duration + "s to complete the action.");
                    }

                    execute();

                case "2":
                    chooseProgram();
                    execute();

                case "3":
                    start();
                    execute();

                case "4":
                    stop();
                    execute();

                case "5":
                    System.out.println("Returning to main menu.");

                default:
                    System.out.println("Wrong Input");
                    execute();
            }
        }
    }
}