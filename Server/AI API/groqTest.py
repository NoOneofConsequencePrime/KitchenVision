server_secret = "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"
input_list = ["white bread", "whole wheat bread", "baguettes", "iced coffee", "espresso", "iced tea", "green tea", "red tea"]
from groq import Groq
import os
import csv
import math

def split_into_chunks(lst, chunk_size):
    """Splits a large list into smaller chunks of a specified size."""
    return [lst[i:i + chunk_size] for i in range(0, len(lst), chunk_size)]

with open("r8.txt", 'r') as f:
    strInp = f.read().strip()

with open ("ingredients.txt", "r") as f:
    line = f.readline().strip("\n").split(",")

# chunks = split_into_chunks(line, 200)

ingredients = ", ".join(line)

strInp = ''.join(e for e in strInp if e.isalnum())

client = Groq(
    api_key=server_secret,
)
pickOut = client.chat.completions.create(
    messages=[
        {
            "role": "system",
            "content": "Given this receipt translated by CV, please pick out the listings for items that were purchased and return them as raw names with a comma and space as delimiters and with no other text such as a response to the prompt."
        },
        {
            "role": "user",
            "content": strInp
        }
    ],
    model= "llama3-8b-8192"
)
vals = pickOut.choices[0].message.content.split(", ")
print(vals)

messagesPrompt = []


chat_completion = client.chat.completions.create(
    messages=[
        {
            "role": "system",
            # "content": "I want to identify a list of ingredients for cooking. However, I don't want the specific type of categorization, I only want the general meaning of the item. For example, if I give you a list containing canola oil, sesame oil, and olive oil, I want you to organize them all as 'cooking oil'. Please return just the raw values with commas as delimiters without any other text.",
            "content": "Add the following list of categories to memory and do not generate a response when completed:"
        },
        {
            "role": "system",
            "content": ingredients
        }
    ],
    model="llama3-8b-8192",
)

print(chat_completion.choices[0].message.content)

# vals = chat_completion.choices[0].message.content.split(", ")


print(vals)
