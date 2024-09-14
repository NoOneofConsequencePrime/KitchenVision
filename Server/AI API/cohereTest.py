import cohere
server_secret = "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"
# input_list = ["white bread", "whole wheat bread", "baguettes", "iced coffee", "espresso", "iced tea", "green tea", "red tea"]
# from groq import Groq
# import os
# import csv
# import math
# import json

co = cohere.Client("vnEjPoyzuplyx7z0PuB3Aso29f9XKDbOXjU9CB5n")

with open("r8.txt", 'r') as f:
    strInp = f.read().strip()

with open ("ingredients.txt", "r") as f:
    line = f.readline().strip("\n").split(",")

# chunks = split_into_chunks(line, 200)

ingredients = ", ".join(line)

strInp = ''.join(e for e in strInp if e.isalnum())

response = co.chat(
    model = "command-r-plus-08-2024",
    # response_format= "text",
    # chat_history=[
        # {"role": "USER", "message": "Add the following list of categories to memory and do not generate a response when completed:"},
        # {"role": "USER", "message": ingredients}
    # ],
    # message="Classify these grocery items into these categories: '" + ingredients + "' and return it in a python dictionary format: " + ", ".join(vals),
    message = "Given the following raw translation from a receipt, pick out the grocery items that were purchased and return them as raw names in a list format and with no other text at all!!!: '" + strInp +"'"
    # perform web search before answering the question. You can also use your own custom connector.
    # connectors=[{"id": "web-search"}],
)
for i in response:
    if i[0] == 'text':
        val = i[1].strip().split(', ')

print(val)

response1 = co.chat(
    model = "command-r-plus-08-2024",
    message = "Given the following list of grocery items, I want you to simplify a bit (only a bit) and return the modified items back to me as a list. For example, 'canola oil' should be just 'oil', 'green herbal tea' should be just 'green tea', ONLY RETURN THE LIST AND NOTHING ELSE IN YOUR RESPONSE!!!: " + ",".join(val)
)
for i in response1:
    if i[0] == 'text':
        val = i[1].strip().split(', ')

print(val)

# val = ["Bun", "Tomato", "Pear", "Bean Green", "Pork back rib"]

response2 = co.chat(
    model = "command-r-plus-08-2024",
    # chat_history=[
        # {"role": "USER", "message": "Add the following list of categories to memory and do not generate a response when completed:"},
        # {"role": "USER", "message": ingredients}
    # ],
    # message="Classify these grocery items into these categories: '" + ingredients + "' and return it in a python dictionary format: " + ", ".join(vals),
    message = "Given the following list of grocery items: '" + ingredients + "', here is another list of ingredients, I want you to match items in this list: '" + ", ".join(val) + "' to the first list as much as possible, if you really can't find a match then just ignore the item and move on. However, keep in mind that there may be some irrelevant description words in each item. Return the matched result with a comma and space between each item. Although the name in the first and second doesn\'t have to match exactly, items in the returned list must match names in the first list EXACTLY!!!!! Remember that some item names have more than 1 word!!"
    # perform web search before answering the question. You can also use your own custom connector.
    # connectors=[{"id": "web-search"}],
)
for i in response2:
    if i[0] == 'text':
        res = i[1].split(',\n-')

print(res)