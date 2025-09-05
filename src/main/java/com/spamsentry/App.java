package com.spamsentry;

import java.nio.file.Path;
import java.nio.file.Paths;

public class App {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: java -jar spamsentry.jar <train|classify> [options]");
            System.exit(1);
        }

        String cmd = args[0];
        if ("train".equals(cmd)) {
            if (args.length < 3) {
                System.out.println("train <csv-file> <model-out>");
                System.exit(1);
            }
            Path csv = Paths.get(args[1]);
            Path model = Paths.get(args[2]);
            Trainer.train(csv.toString(), model.toString());
            System.out.println("Model written to: " + model);
        } else if ("classify".equals(cmd)) {
            if (args.length < 3) {
                System.out.println("classify <model-file> <text-to-classify>");
                System.exit(1);
            }
            String model = args[1];
            String text = args[2];
            String label = ClassifierUtil.classify(model, text);
            System.out.println(label);
        } else {
            // add eval command
            if ("eval".equals(cmd)) {
                if (args.length < 3) {
                    System.out.println("eval <csv-file> <model-out> [folds]");
                    System.exit(1);
                }
                String csv = args[1];
                String model = args[2];
                int folds = 5;
                if (args.length >= 4) {
                    folds = Integer.parseInt(args[3]);
                }
                Trainer.trainWithEval(csv, model, folds);
                System.out.println("Model written to: " + model);
                return;
            }
            if ("probs".equals(cmd)) {
                if (args.length < 3) {
                    System.out.println("probs <model-file> <text>");
                    System.exit(1);
                }
                String model = args[1];
                String text = args[2];
                double[] d = ClassifierUtil.distribution(model, text);
                System.out.printf("p(0)=%.4f p(1)=%.4f\n", d[0], d[1]);
                return;
            }
            if ("evalclf".equals(cmd)) {
                if (args.length < 4) {
                    System.out.println("evalclf <csv-file> <model-out> <classifier> [folds]");
                    System.exit(1);
                }
                String csv = args[1];
                String model = args[2];
                String clf = args[3];
                int folds = 5;
                if (args.length >= 5) folds = Integer.parseInt(args[4]);
                Trainer.trainWithEvalClassifier(csv, model, folds, clf);
                System.out.println("Model written to: " + model);
                return;
            }
            System.out.println("Unknown command: " + cmd);
            System.exit(1);
        }
    }
}
