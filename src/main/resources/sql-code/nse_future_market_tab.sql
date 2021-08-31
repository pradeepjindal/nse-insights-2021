CREATE SEQUENCE public.nse_future_market_seq_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.nse_future_market_seq_id OWNER TO postgres;


CREATE TABLE public.nse_future_market_tab (
                                              id bigint DEFAULT nextval('public.nse_future_market_seq_id'::regclass) NOT NULL,
                                              instrument character varying(16) NOT NULL,
                                              symbol character varying(16) NOT NULL,
                                              expiry_date date NOT NULL,
                                              strike_price numeric(18,2) NOT NULL,
                                              option_type character varying(16) NOT NULL,
                                              open numeric(18,2),
                                              high numeric(18,2),
                                              low numeric(18,2),
                                              close numeric(18,2),
                                              settle_price numeric(18,2),
                                              contracts bigint,
                                              value_in_lakh numeric(18,2),
                                              open_int bigint,
                                              change_in_oi bigint,
                                              trade_date date NOT NULL,
                                              tds character varying(10)
);

ALTER TABLE public.nse_future_market_tab OWNER TO postgres;



ALTER TABLE ONLY public.nse_future_market_tab
    ADD CONSTRAINT nse_future_market_constraint_unique_colums UNIQUE (trade_date, expiry_date, symbol, instrument, strike_price, option_type);

ALTER TABLE ONLY public.nse_future_market_tab
    ADD CONSTRAINT nse_future_market_tab_pkey PRIMARY KEY (id);



CREATE INDEX nse_future_market_composite_idx1 ON public.nse_future_market_tab USING btree (trade_date, expiry_date, instrument, option_type, symbol, strike_price);

CREATE INDEX nse_future_market_pk_idx ON public.nse_future_market_tab USING hash (id);
