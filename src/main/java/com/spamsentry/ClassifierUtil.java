package com.spamsentry;

import weka.classifiers.Classifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.filters.Filter;

import java.util.ArrayList;

public class ClassifierUtil {
    public static String classify(String modelPath, String text) throws Exception {
    Object obj = SerializationHelper.read(modelPath);
    Classifier cls = (Classifier) obj;

    // Recreate structure: a string attribute and class {0,1}
        ArrayList<Attribute> attrs = new ArrayList<>();
        attrs.add(new Attribute("text", (ArrayList<String>) null));
        ArrayList<String> classVals = new ArrayList<>();
        classVals.add("0");
        classVals.add("1");
        attrs.add(new Attribute("spam", classVals));

        Instances data = new Instances("emails", attrs, 1);
        data.setClassIndex(1);

    String cleaned = TextPreprocessor.normalize(text);
    double[] vals = new double[data.numAttributes()];
    vals[0] = data.attribute(0).addStringValue(cleaned);
        vals[1] = 0; // placeholder
        Instance inst = new DenseInstance(1.0, vals);
        data.add(inst);

        Instances toClassify = data;
        double pred;
        if (cls instanceof FilteredClassifier) {
            FilteredClassifier fcls = (FilteredClassifier) cls;
            // FilteredClassifier will handle filtering internally if we pass the original instance structure
            pred = fcls.classifyInstance(toClassify.instance(0));
        } else {
            // Fallback: try to classify raw
            pred = cls.classifyInstance(toClassify.instance(0));
        }
        return data.classAttribute().value((int) pred);
    }

    public static double[] distribution(String modelPath, String text) throws Exception {
        Object obj = SerializationHelper.read(modelPath);
        Classifier cls = (Classifier) obj;

        ArrayList<Attribute> attrs = new ArrayList<>();
        attrs.add(new Attribute("text", (ArrayList<String>) null));
        ArrayList<String> classVals = new ArrayList<>();
        classVals.add("0");
        classVals.add("1");
        attrs.add(new Attribute("spam", classVals));

        Instances data = new Instances("emails", attrs, 1);
        data.setClassIndex(1);

        String cleaned = TextPreprocessor.normalize(text);
        double[] vals = new double[data.numAttributes()];
        vals[0] = data.attribute(0).addStringValue(cleaned);
        vals[1] = 0;
        Instance inst = new DenseInstance(1.0, vals);
        data.add(inst);

        double[] dist;
        if (cls instanceof FilteredClassifier) {
            FilteredClassifier fcls = (FilteredClassifier) cls;
            dist = fcls.distributionForInstance(data.instance(0));
        } else {
            dist = cls.distributionForInstance(data.instance(0));
        }
        return dist;
    }
}
