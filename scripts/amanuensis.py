import json

import psycopg2
import os
import os.path
import requests
import torch
import torch.nn.functional as F

import sys

db_url = "postgresql://localhost:5432/symbiotic"


def embedding(content: str):
    response = requests.post(
        url="http://localhost:11434/api/embeddings",
        json={
            "model": "mistral:7b",
            "prompt": content
        },
        headers={
            "Content-Type": "application/json"
        })
    return response.json()


device = torch.device("mps")
m = torch.nn.Upsample(size=(1, 256), mode="bilinear").to(device)


def upsample(embedding):
    ipt = torch.FloatTensor(embedding)
    ipt = torch.autograd.Variable(ipt.unsqueeze(0).unsqueeze(0).unsqueeze(0))
    return m(ipt).squeeze(0).squeeze(0).squeeze(0).tolist()


def read_file(path, encode='utf-8'):
    with open(path, mode="br") as f:
        content = f.read()
        try:
            return content.decode("utf-16")
        except UnicodeDecodeError as err:
            return content.decode("gb18030")


if __name__ == "__main__":
    article = sys.argv[1]

    with psycopg2.connect("postgresql://localhost:5432/symbiotic") as conn:
        content = read_file(article)
        reply = embedding(content)
        emb = reply["embedding"]
        down = upsample(emb)
        p = os.path.abspath(article)
        meta = {"path": p,
                "model": "phi:latest",
                "ext": os.path.splitext(p)[-1],
                "filename": os.path.split(p)[-1]}
        with conn.cursor() as cursor:
            cursor.execute("insert into library(meta, content, ollama, upsample) values (%s, %s, %s, %s)",
                           (json.dumps(meta), content,
                            json.dumps(emb), json.dumps(down)))
        conn.commit()
        print(article)
