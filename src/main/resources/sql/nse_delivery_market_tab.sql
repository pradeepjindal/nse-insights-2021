CREATE SEQUENCE public.nse_delivery_market_seq_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.nse_delivery_market_seq_id OWNER TO postgres;


CREATE TABLE public.nse_delivery_market_tab (
                                                id bigint DEFAULT nextval('public.nse_delivery_market_seq_id'::regclass) NOT NULL,
                                                symbol character varying(16) NOT NULL,
                                                security_type character varying(16) NOT NULL,
                                                traded_qty bigint,
                                                deliverable_qty bigint,
                                                delivery_to_trade_ratio numeric(18,2),
                                                trade_date date NOT NULL
);

ALTER TABLE public.nse_delivery_market_tab OWNER TO postgres;



ALTER TABLE ONLY public.nse_delivery_market_tab
    ADD CONSTRAINT nse_delivery_market_constraint_unique_colums UNIQUE (trade_date, symbol, security_type);

ALTER TABLE ONLY public.nse_delivery_market_tab
    ADD CONSTRAINT nse_delivery_market_tab_pkey PRIMARY KEY (id);



CREATE INDEX nse_delivery_market_composite_idx1 ON public.nse_delivery_market_tab USING btree (trade_date, security_type, symbol);

CREATE INDEX nse_delivery_market_pk_idx ON public.nse_delivery_market_tab USING hash (id);
