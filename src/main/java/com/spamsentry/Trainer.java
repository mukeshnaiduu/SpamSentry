package com.spamsentry;

import com.opencsv.CSVReader;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class Trainer {
    public static void train(String csvPath, String modelOut) throws Exception {
        // Read CSV: expects header text,spam and rows: "...text...",0/1 or 1/0
        CSVReader r = new CSVReader(new FileReader(csvPath));
        String[] header = r.readNext(); // skip header

        ArrayList<Attribute> attrs = new ArrayList<>();
        attrs.add(new Attribute("text", (ArrayList<String>) null)); // string attr
        ArrayList<String> classVals = new ArrayList<>();
        classVals.add("0");
        classVals.add("1");
        attrs.add(new Attribute("spam", classVals));

        Instances data = new Instances("emails", attrs, 1000);
        data.setClassIndex(1);

        String[] row;
        while ((row = r.readNext()) != null) {
            if (row.length < 2) continue;
            double[] vals = new double[data.numAttributes()];
            String cleaned = TextPreprocessor.normalize(row[0]);
            vals[0] = data.attribute(0).addStringValue(cleaned);
            String cls = row[1].trim();
            if (!classVals.contains(cls)) {
                // try alternate 0/1 numeric
                if (cls.equals("spam") || cls.equalsIgnoreCase("true")) cls = "1";
                else cls = cls.equals("1") ? "1" : "0";
            }
            vals[1] = classVals.indexOf(cls);
            data.add(new DenseInstance(1.0, vals));
        }
        r.close();

    // Configure StringToWordVector and wrap into FilteredClassifier so the
    // filter state (dictionary) is persisted with the classifier.
    StringToWordVector filter = new StringToWordVector();
    filter.setLowerCaseTokens(true);
    filter.setOutputWordCounts(true);
    // use attribute prefix
    filter.setAttributeNamePrefix("T_");
    // remove common English stopwords
    filter.setStopwordsHandler(new weka.core.stopwords.Rainbow());
    // minimum term frequency across documents (1 = keep all; set to 2 to reduce noise)
    filter.setMinTermFreq(2);

    NaiveBayes nb = new NaiveBayes();
    FilteredClassifier fc = new FilteredClassifier();
    fc.setFilter(filter);
    fc.setClassifier(nb);
    fc.buildClassifier(data);

    // Save the FilteredClassifier (it includes the filter state)
    SerializationHelper.write(modelOut, fc);
    }

    public static void trainWithEval(String csvPath, String modelOut, int folds) throws Exception {
        // Build dataset (reuse train code's CSV parsing)
        CSVReader r = new CSVReader(new FileReader(csvPath));
        String[] header = r.readNext();

        ArrayList<Attribute> attrs = new ArrayList<>();
        attrs.add(new Attribute("text", (ArrayList<String>) null));
        ArrayList<String> classVals = new ArrayList<>();
        classVals.add("0");
        classVals.add("1");
        attrs.add(new Attribute("spam", classVals));

        Instances data = new Instances("emails", attrs, 1000);
        data.setClassIndex(1);

        String[] row;
        while ((row = r.readNext()) != null) {
            if (row.length < 2) continue;
            double[] vals = new double[data.numAttributes()];
            String cleaned = TextPreprocessor.normalize(row[0]);
            vals[0] = data.attribute(0).addStringValue(cleaned);
            String cls = row[1].trim();
            vals[1] = classVals.indexOf(cls.equals("1") ? "1" : "0");
            data.add(new DenseInstance(1.0, vals));
        }
        r.close();

        // Configure filter + classifier
    StringToWordVector filter = new StringToWordVector();
    filter.setLowerCaseTokens(true);
    filter.setOutputWordCounts(true);
    filter.setAttributeNamePrefix("T_");
    filter.setStopwordsHandler(new weka.core.stopwords.Rainbow());
    filter.setMinTermFreq(2);

        NaiveBayes nb = new NaiveBayes();
        FilteredClassifier fc = new FilteredClassifier();
        fc.setFilter(filter);
        fc.setClassifier(nb);

        // Evaluate with cross-validation
        weka.classifiers.Evaluation eval = new weka.classifiers.Evaluation(data);
        eval.crossValidateModel(fc, data, folds, new java.util.Random(1));

        System.out.printf("Cross-validation (%d folds) accuracy: %.4f\n", folds, (1 - eval.errorRate()));
        System.out.println(eval.toMatrixString("Confusion matrix"));

        // Train final model on all data and save
        fc.buildClassifier(data);
        SerializationHelper.write(modelOut, fc);
    }

    // Train with evaluation but accept any classifier (by name) for quick experiments
    public static void trainWithEvalClassifier(String csvPath, String modelOut, int folds, String classifierName) throws Exception {
        // Build dataset (reuse parsing)
        CSVReader r = new CSVReader(new FileReader(csvPath));
        String[] header = r.readNext();

        ArrayList<Attribute> attrs = new ArrayList<>();
        attrs.add(new Attribute("text", (ArrayList<String>) null));
        ArrayList<String> classVals = new ArrayList<>();
        classVals.add("0");
        classVals.add("1");
        attrs.add(new Attribute("spam", classVals));

        Instances data = new Instances("emails", attrs, 1000);
        data.setClassIndex(1);

        String[] row;
        while ((row = r.readNext()) != null) {
            if (row.length < 2) continue;
            double[] vals = new double[data.numAttributes()];
            String cleaned = TextPreprocessor.normalize(row[0]);
            vals[0] = data.attribute(0).addStringValue(cleaned);
            String cls = row[1].trim();
            vals[1] = classVals.indexOf(cls.equals("1") ? "1" : "0");
            data.add(new DenseInstance(1.0, vals));
        }
        r.close();

        StringToWordVector filter = new StringToWordVector();
        filter.setLowerCaseTokens(true);
        filter.setOutputWordCounts(true);
        filter.setAttributeNamePrefix("T_");
        filter.setStopwordsHandler(new weka.core.stopwords.Rainbow());
        filter.setMinTermFreq(2);

        weka.classifiers.Classifier base;
        if ("logistic".equalsIgnoreCase(classifierName)) {
            base = new weka.classifiers.functions.Logistic();
        } else if ("rf".equalsIgnoreCase(classifierName) || "randomforest".equalsIgnoreCase(classifierName)) {
            base = new weka.classifiers.trees.RandomForest();
        } else {
            base = new NaiveBayes();
        }

        FilteredClassifier fc = new FilteredClassifier();
        fc.setFilter(filter);
        fc.setClassifier(base);

        weka.classifiers.Evaluation eval = new weka.classifiers.Evaluation(data);
        eval.crossValidateModel(fc, data, folds, new java.util.Random(1));

        System.out.printf("Cross-validation (%d folds) accuracy: %.4f\n", folds, (1 - eval.errorRate()));
        System.out.println(eval.toMatrixString("Confusion matrix"));

        fc.buildClassifier(data);
        SerializationHelper.write(modelOut, fc);
    }
}
