import pandas as pd
import matplotlib.pyplot as plt

# Load CSV data
df = pd.read_csv("detection_results.csv")

# Display results
print("\n=== Detection Results ===")
print(df.to_string(index=False))

# Count occurrences of classifications
counts = df["Classification"].value_counts()

# Create Pie Chart
plt.figure(figsize=(6,6))
plt.pie(counts, labels=counts.index, autopct="%1.1f%%", colors=["green", "red"])
plt.title("Deepfake vs Real Images")
plt.savefig("classification_chart.png")
plt.show()

# Create Bar Chart to Show Scores
plt.figure(figsize=(8,5))
plt.barh(df["Image Name"], df["Score"], color=['red' if x == "Deepfake" else 'green' for x in df["Classification"]])
plt.xlabel("Score")
plt.ylabel("Image Name")
plt.title("Deepfake Detection Scores")
plt.savefig("score_chart.png")
plt.show()

print("\nPie chart saved as classification_chart.png")
print("Bar chart saved as score_chart.png")
