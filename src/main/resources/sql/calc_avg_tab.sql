CREATE SEQUENCE public.calc_avg_seq_new
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.calc_avg_seq_new OWNER TO postgres;



CREATE TABLE public.calc_avg_tab_new (
    id bigint DEFAULT nextval('public.calc_avg_seq_new'::regclass) NOT NULL,
    symbol character varying(16) NOT NULL,
    trade_date date NOT NULL,
    tds character varying(9),
    for_days integer NOT NULL,
    atp_sma numeric(18,2),
    vol_sma numeric(18,2),
    del_sma numeric(18,2),
    foi_sma numeric(18,2)
);

ALTER TABLE public.calc_avg_tab_new OWNER TO postgres;



ALTER TABLE ONLY public.calc_avg_tab_new
    ADD CONSTRAINT calc_avg_unique_new UNIQUE (symbol, trade_date, for_days);
