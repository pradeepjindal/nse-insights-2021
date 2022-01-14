CREATE SEQUENCE public.nse_lot_size_seq_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.nse_lot_size_seq_id OWNER TO postgres;


CREATE TABLE public.nse_lot_size_tab (
    id bigint DEFAULT nextval('public.nse_lot_size_seq_id'::regclass) NOT NULL,
    symbol character(12) NOT NULL,
    trade_date date NOT NULL,
    tdn integer,
    expiry_date date NOT NULL,
    edn integer,
    lot_size integer NOT NULL,
    file_date date NOT NULL,
    fdn integer,
    comment character varying(32)
);

ALTER TABLE public.nse_lot_size_tab OWNER TO postgres;



ALTER TABLE ONLY public.nse_lot_size_tab
    ADD CONSTRAINT nse_lot_size_constraint_unique_colums UNIQUE (trade_date, expiry_date, symbol);

ALTER TABLE ONLY public.nse_lot_size_tab
    ADD CONSTRAINT nse_lot_size_tab_pkey PRIMARY KEY (id);



CREATE INDEX nse_lot_size_composite_idx1 ON public.nse_lot_size_tab USING btree (trade_date, expiry_date, symbol);

CREATE INDEX nse_lot_size_pk_idx ON public.nse_lot_size_tab USING hash (id);
