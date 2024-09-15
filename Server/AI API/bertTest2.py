from transformers import BertTokenizer, BertModel
import torch
import numpy as np

# Load pre-trained BERT model and tokenizer
tokenizer = BertTokenizer.from_pretrained('bert-base-uncased')
model = BertModel.from_pretrained('bert-base-uncased')

# Function to get BERT embeddings for an input text
def get_bert_embedding(text):
    inputs = tokenizer(text, return_tensors="pt", padding=True, truncation=True)
    with torch.no_grad():
        outputs = model(**inputs)
        embeddings = outputs.last_hidden_state.mean(dim=2)  # Mean pooling of tokens
    return embeddings

# Example items
item_1 = "YOGURT Mocha Liber"
item_2 = "Bun - Portuguese"

# Get embeddings
embedding_1 = get_bert_embedding(item_1)
embedding_2 = get_bert_embedding(item_2)



from sklearn.metrics.pairwise import cosine_similarity

# Calculate cosine similarity between embeddings
similarity = cosine_similarity(embedding_1, embedding_2)
print(f"Cosine Similarity between {item_1} and {item_2}: {similarity[0][0]}")

# Define a threshold for classifying items
similarity_threshold = 0.8

def classify_items(embedding_1, embedding_2, threshold=similarity_threshold):
    similarity = cosine_similarity(embedding_1, embedding_2)[0][0]
    if similarity > threshold:
        return "Similar"
    else:
        return "Different"

# Example classification
classification = classify_items(embedding_1, embedding_2)
print(f"Classification result: {classification}")
