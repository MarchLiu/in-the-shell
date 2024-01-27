create table session
(
    id          bigserial primary key,
    session_id  bigint,
    meta        jsonb     default '{}'::jsonb,
    request_at  timestamp,
    response_at timestamp,
    log_at      timestamp default now(),
    request     text,
    response    text
);
create index on session (id);
create index on session using gin (meta);

create extension vector;
create table library
(
    id       bigserial primary key,
    meta     jsonb     default '{}'::jsonb,
    log_at   timestamp default now(),
    content  text,
    ollama   vector(4096),
    upsample vector(256),
    top256   vector(256)
);

create index on library using gin (meta);
CREATE INDEX ON library USING hnsw (upsample vector_l2_ops);
CREATE INDEX ON library USING hnsw (top256 vector_l2_ops);

create or replace function take256(ipt vector(4096)) returns vector(256)
as
$$
select array_agg(t.e)
from (select e
      from unnest(ipt::float4[]) as t(e)
      limit 256) as t
$$ language SQL