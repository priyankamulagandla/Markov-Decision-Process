import org.apache.commons.cli.*;

public class MdpSolver {
    public static void main(String[] args) {
        Options commandOptions = new Options();
        Option inputFilePath = new Option("input", "input-file", true,
                "input file path");
        Option verboseOption = new Option("v", "verbose", false, "print verbose output");
        Option discountFactor = new Option("df", true, "discount factor");
        Option tolerance = new Option("tol", true, "tolerance");
        Option minimize = new Option("min", true, "minimize");
        Option iterations = new Option("iter", true, "Iteration Count");

        commandOptions.addOption(inputFilePath);
        commandOptions.addOption(verboseOption);
        commandOptions.addOption(discountFactor);
        commandOptions.addOption(tolerance);
        commandOptions.addOption(minimize);
        commandOptions.addOption(iterations);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;
        try{
            cmd = parser.parse(commandOptions, args);
        } catch(ParseException e){
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", commandOptions);
            System.exit(1);
        }

        boolean verbose = cmd.hasOption("verbose");
        String inputFile = cmd.getOptionValue("input");
        boolean min = cmd.hasOption("min") && Boolean.valueOf(cmd.getOptionValue("min"));
        double tol = cmd.hasOption("tol") ? Double.parseDouble(cmd.getOptionValue("tol")) : 0.01;
        int iter = cmd.hasOption("iter") ? Integer.parseInt(cmd.getOptionValue("iter")) : 100;
        double df = cmd.hasOption("df") ? Double.parseDouble(cmd.getOptionValue("df")) : 1.0;
        Solver.solver(inputFile, verbose, df, iter, tol, min);
    }
}
