CREATE SEQUENCE public.calc_mfi_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.calc_mfi_seq OWNER TO postgres;



CREATE TABLE public.calc_mfi_tab (
    id bigint DEFAULT nextval('public.calc_mfi_seq'::regclass) NOT NULL,
    symbol character varying(16) NOT NULL,
    trade_date date NOT NULL,
    tds character varying(10),
    for_days integer NOT NULL,
    vol_atp_mfi_sma numeric(18,2),
    del_atp_mfi_sma numeric(18,2)
);

ALTER TABLE public.calc_mfi_tab OWNER TO postgres;



ALTER TABLE ONLY public.calc_mfi_tab
    ADD CONSTRAINT calc_mfi_unique UNIQUE (symbol, trade_date, for_days);
