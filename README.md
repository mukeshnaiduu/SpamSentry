SpamSentry - Minimal Weka MVP
# SpamSentry

SpamSentry is a minimal Java CLI for experimenting with email spam classification using Weka. It includes:

- CSV reading and text preprocessing (URL/email/number masking, lowercasing, punctuation removal)
- Feature extraction with Weka's StringToWordVector
- Training and evaluation with multiple classifiers (NaiveBayes, Logistic, RandomForest)
- Model persistence and single-text classification

## Build

Build the fat JAR with Maven:

```bash
mvn -DskipTests package
```

The runnable JAR is at `target/spamsentry-0.1.0.jar` (shaded).

## Data

- Original dataset: `data/emails.csv`
- Cleaned dataset produced by the project: `data/emails_clean.csv` (normalized text and labels 0/1)

## CLI

Usage (examples):

- Train a NaiveBayes model:

```bash
java -jar target/spamsentry-0.1.0.jar train data/emails_clean.csv models/nb.model
```

- Train with evaluation (k-fold) and save model:

```bash
java -jar target/spamsentry-0.1.0.jar eval data/emails_clean.csv models/nb.model 5
```

- Get per-class probabilities for a text:

```bash
java -jar target/spamsentry-0.1.0.jar probs models/logistic_clean.model "Your text here"
# prints: p(0)=... p(1)=...
```

- Quick experiments with different classifiers (NaiveBayes, logistic, rf):

```bash
java -jar target/spamsentry-0.1.0.jar evalclf data/emails_clean.csv models/logistic_clean.model logistic 5
java -jar target/spamsentry-0.1.0.jar evalclf data/emails_clean.csv models/rf_clean.model rf 5
```

- Classify a single text (uses the model's default decision rule):

```bash
java -jar target/spamsentry-0.1.0.jar classify models/logistic_clean.model "Free trial, click now"
```

## Files of interest

- `src/main/java/com/spamsentry/Trainer.java` — CSV reading, preprocessing, filter + classifier training, CV evaluation
- `src/main/java/com/spamsentry/TextPreprocessor.java` — normalization & masking
- `src/main/java/com/spamsentry/ClassifierUtil.java` — load model, classify, distribution
- `src/main/java/com/spamsentry/DataCleaner.java` — produces `data/emails_clean.csv`

## Recommended next steps

- Compare classifiers on held-out data and choose an operating threshold for p(spam) for your precision/recall needs.
- Add unit tests for `TextPreprocessor.normalize(...)` and the classification utilities.
- Persist the chosen model as the default and add a small HTTP wrapper for live classification.

## Quick results from this workspace

- NaiveBayes (clean data) — CV accuracy ~0.881
- Logistic (clean data) — CV accuracy ~0.968
- RandomForest (clean data) — CV accuracy ~0.986

These runs and the saved models (`models/*.model`) are available in the workspace.

## How I tested

I ran small batches of example texts with `classify`/`probs` to verify behavior. See the `examples.txt` used during testing in the workspace root.

---
If you want, I can add an evaluation report (precision/recall at several thresholds) and a small script to run bulk classification on new CSVs.
