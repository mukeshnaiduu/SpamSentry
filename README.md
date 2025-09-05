SpamSentry - Minimal Weka MVP

This is a tiny MVP for the SpamSentry project. It trains a Weka Multinomial Naive Bayes model on a small sample dataset and provides a CLI to train and classify single texts.

Quick start

Build:

```bash
mvn package
```

Train on sample data:

```bash
java -jar target/spamsentry-0.1.0-jar-with-dependencies.jar train data/sample models/nb.model
```

Classify a text:

```bash
java -jar target/spamsentry-0.1.0-jar-with-dependencies.jar classify models/nb.model "Free money, click now!"
```

Project layout

- `src/main/java` - source
- `src/test/java` - tests
- `data/sample` - tiny sample data (ham/spam)

Notes

- This is intentionally minimal for a 1-day demo. For production use, expand preprocessing, add tests, and improve dataset.
# SpamSentry
SpamSentry is a Java-based machine learning project that classifies emails as spam or ham using the Enron Email Dataset. It leverages Weka/Smile with algorithms like Naive Bayes and Logistic Regression, featuring text preprocessing, model training, and evaluation to keep inboxes clean.
