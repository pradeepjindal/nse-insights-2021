CREATE SEQUENCE public.nse_fo_seq_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.nse_fo_seq_id OWNER TO postgres;


CREATE TABLE public.nse_fo_tab (
    id bigint DEFAULT nextval('public.nse_fo_seq_id'::regclass) NOT NULL,
    symbol character(10) NOT NULL,
    trade_date date NOT NULL,
    expiry_date date NOT NULL,
    instrument character (6) NOT NULL,
    quantity integer NOT NULL,
    contracts integer NOT NULL,
    lot_size integer NOT NULL,
    turnover numeric (18,2) NOT NULL,
    file_date date NOT NULL,
    tdn integer,
    edn integer,
    fdn integer,
    nse character varying(16)
);

ALTER TABLE public.nse_fo_tab OWNER TO postgres;



ALTER TABLE ONLY public.nse_fo_tab
    ADD CONSTRAINT nse_fo_constraint_unique_colums UNIQUE (trade_date, expiry_date, symbol);

ALTER TABLE ONLY public.nse_fo_tab
    ADD CONSTRAINT nse_fo_tab_pkey PRIMARY KEY (id);



CREATE INDEX nse_fo_composite_idx1 ON public.nse_fo_tab USING btree (trade_date, expiry_date, symbol);

CREATE INDEX nse_fo_pk_idx ON public.nse_fo_tab USING hash (id);
