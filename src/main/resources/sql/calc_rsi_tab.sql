CREATE SEQUENCE public.calc_rsi_seq_new
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.calc_rsi_seq_new OWNER TO postgres;



CREATE TABLE public.calc_rsi_tab_new (
    id bigint DEFAULT nextval('public.calc_rsi_seq_new'::regclass) NOT NULL,
    symbol character varying(16) NOT NULL,
    trade_date date NOT NULL,
    tds character varying(9),
    for_days integer NOT NULL,
    open_rsi_sma numeric(18,2),
    high_rsi_sma numeric(18,2),
    low_rsi_sma numeric(18,2),
    close_rsi_sma numeric(18,2),
    last_rsi_sma numeric(18,2),
    atp_rsi_sma numeric(18,2),
    hlm_rsi_sma numeric(18,2),
    ohlc_rsi_sma numeric(18,2),
    del_rsi_sma numeric(18,2)
);

ALTER TABLE public.calc_rsi_tab_new OWNER TO postgres;



ALTER TABLE ONLY public.calc_rsi_tab_new
    ADD CONSTRAINT calc_rsi_unique_new UNIQUE (symbol, trade_date, for_days);
