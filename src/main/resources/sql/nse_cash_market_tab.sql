CREATE SEQUENCE public.nse_cash_market_seq_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.nse_cash_market_seq_id OWNER TO postgres;


CREATE TABLE public.nse_cash_market_tab (
                                            id bigint DEFAULT nextval('public.nse_cash_market_seq_id'::regclass) NOT NULL,
                                            symbol character varying(16) NOT NULL,
                                            series character varying(16),
                                            open numeric(18,2),
                                            high numeric(18,2),
                                            low numeric(18,2),
                                            close numeric(18,2),
                                            last numeric(18,2),
                                            prev_close numeric(18,2),
                                            tot_trd_qty bigint,
                                            tot_trd_val numeric(18,2),
                                            trade_date date,
                                            total_trades bigint,
                                            isin character(16)
);

ALTER TABLE public.nse_cash_market_tab OWNER TO postgres;



ALTER TABLE ONLY public.nse_cash_market_tab
    ADD CONSTRAINT nse_cash_market_constraint_unique_colums UNIQUE (trade_date, symbol);

ALTER TABLE ONLY public.nse_cash_market_tab
    ADD CONSTRAINT nse_cash_market_tab_pkey PRIMARY KEY (id);



CREATE INDEX nse_cash_market_composite_idx1 ON public.nse_cash_market_tab USING btree (trade_date, symbol);

CREATE INDEX nse_cash_market_pk_idx ON public.nse_cash_market_tab USING hash (id);
