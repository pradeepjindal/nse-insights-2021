CREATE SEQUENCE public.nse_index_market_seq_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.nse_index_seq_id OWNER TO postgres;


CREATE TABLE public.nse_index_tab (
    id bigint DEFAULT nextval('public.nse_index_seq_id'::regclass) NOT NULL,
    symbol character varying(32) NOT NULL,
    idx_name character varying(32),
    trade_date date NOT NULL,
    tds character varying(10),
    open numeric(18,2),
    high numeric(18,2),
    low numeric(18,2),
    close numeric(18,2),
    Points_Chg_Abs numeric(18,2),
    Points_Chg_Pct numeric(18,2),
    volume bigint,
    turn_Over_In_Crore numeric(18,2),
    pe numeric(18,2),
    pb numeric(18,2),
    div_Yield numeric(18,2)
);

ALTER TABLE public.nse_index_tab OWNER TO postgres;



ALTER TABLE ONLY public.nse_index_tab
    ADD CONSTRAINT nse_index_constraint_unique_colums UNIQUE (trade_date, symbol);

ALTER TABLE ONLY public.nse_index_tab
    ADD CONSTRAINT nse_index_tab_pkey PRIMARY KEY (id);



CREATE INDEX nse_index_composite_idx1 ON public.nse_index_tab USING btree (trade_date, symbol);

CREATE INDEX nse_index_pk_idx ON public.nse_index_tab USING hash (id);
