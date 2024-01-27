import json

import requests
import torch
import sys
import psycopg2

device = torch.device("mps")
m = torch.nn.Upsample(size=(1, 256), mode="bilinear").to(device)


def upsample(embedding):
    ipt = torch.FloatTensor(embedding)
    ipt = torch.autograd.Variable(ipt.unsqueeze(0).unsqueeze(0).unsqueeze(0))
    return m(ipt).squeeze(0).squeeze(0).squeeze(0).tolist()


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


if __name__ == "__main__":
    query = sys.argv[1]
    with psycopg2.connect("postgresql://localhost:5432/symbiotic") as conn:
        reply = embedding(query)
        emb = reply["embedding"]
        down = upsample(emb)


        print("=======by embedding=======")
        with conn.cursor() as cursor:
            cursor.execute("select id, meta from library order by ollama <-> %s limit 5",
                           (json.dumps(emb), ))
            for row in cursor.fetchall():
                print(row)

        print("=======by take 256=======")
        with conn.cursor() as cursor:
            cursor.execute("select id, meta from library order by library.top256 <-> %s  limit 5",
                           (json.dumps(down), ))
            for row in cursor.fetchall():
                print(row)

        print("=======by upsample=======")
        with conn.cursor() as cursor:
            cursor.execute("select id, meta from library order by library.upsample <-> %s  limit 5",
                           (json.dumps(down), ))
            for row in cursor.fetchall():
                print(row)
