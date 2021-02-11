--
-- PostgreSQL database dump
--

-- Dumped from database version 13.1
-- Dumped by pg_dump version 13.1

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: import; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA import;


ALTER SCHEMA import OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: fm_lots; Type: TABLE; Schema: import; Owner: postgres
--

CREATE TABLE import.fm_lots (
    symbol text,
    size text
);


ALTER TABLE import.fm_lots OWNER TO postgres;

--
-- Name: calc_avg_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.calc_avg_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.calc_avg_seq OWNER TO postgres;

--
-- Name: calc_avg_seq_new; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.calc_avg_seq_new
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.calc_avg_seq_new OWNER TO postgres;

--
-- Name: calc_avg_tab_new; Type: TABLE; Schema: public; Owner: postgres
--

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

--
-- Name: calc_mfi_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.calc_mfi_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.calc_mfi_seq OWNER TO postgres;

--
-- Name: calc_mfi_seq_new; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.calc_mfi_seq_new
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.calc_mfi_seq_new OWNER TO postgres;

--
-- Name: calc_mfi_tab_new; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.calc_mfi_tab_new (
    id bigint DEFAULT nextval('public.calc_mfi_seq_new'::regclass) NOT NULL,
    symbol character varying(16) NOT NULL,
    trade_date date NOT NULL,
    tds character varying(9),
    for_days integer NOT NULL,
    vol_atp_mfi_sma numeric(18,2),
    del_atp_mfi_sma numeric(18,2)
);


ALTER TABLE public.calc_mfi_tab_new OWNER TO postgres;

--
-- Name: calc_rsi_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.calc_rsi_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.calc_rsi_seq OWNER TO postgres;

--
-- Name: calc_rsi_seq_new; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.calc_rsi_seq_new
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.calc_rsi_seq_new OWNER TO postgres;

--
-- Name: calc_rsi_tab_new; Type: TABLE; Schema: public; Owner: postgres
--

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

--
-- Name: nse_future_market_seq_id; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.nse_future_market_seq_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.nse_future_market_seq_id OWNER TO postgres;

--
-- Name: nse_future_market_tab; Type: TABLE; Schema: public; Owner: postgres
--

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
    trade_date date
);


ALTER TABLE public.nse_future_market_tab OWNER TO postgres;

--
-- Name: min_expiry_view1; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.min_expiry_view1 AS
 SELECT nse_future_market_tab.symbol,
    nse_future_market_tab.trade_date,
    min(nse_future_market_tab.expiry_date) AS min_expiry_date
   FROM public.nse_future_market_tab
  GROUP BY nse_future_market_tab.symbol, nse_future_market_tab.trade_date;


ALTER TABLE public.min_expiry_view1 OWNER TO postgres;

--
-- Name: nse_cash_market_seq_id; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.nse_cash_market_seq_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.nse_cash_market_seq_id OWNER TO postgres;

--
-- Name: nse_cash_market_tab; Type: TABLE; Schema: public; Owner: postgres
--

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

--
-- Name: nse_delivery_market_seq_id; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.nse_delivery_market_seq_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.nse_delivery_market_seq_id OWNER TO postgres;

--
-- Name: nse_delivery_market_tab; Type: TABLE; Schema: public; Owner: postgres
--

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

--
-- Name: cfd_data_all_mv; Type: MATERIALIZED VIEW; Schema: public; Owner: postgres
--

CREATE MATERIALIZED VIEW public.cfd_data_all_mv AS
 SELECT cmt.symbol,
    cmt.trade_date,
    cmt.cmopen,
    cmt.cmhigh,
    cmt.cmlow,
    cmt.cmclose,
    cmt.cmlast,
    cmt.cmclose_prev,
    round((cmt.tot_trd_val / (cmt.tot_trd_qty)::numeric), 2) AS cmatp,
    dmt.traded_qty,
    dmt.delivered_qty,
    dmt.del_to_trd_ratio,
    cmt.tot_trd_qty,
    cmt.tot_trd_val,
    fmt.fuopen,
    fmt.fuhigh,
    fmt.fulow,
    fmt.fuclose,
    fmt.fulast,
    (fmt.fuclose - cmt.cmclose) AS premium,
    fmt.oi
   FROM ((( SELECT nse_cash_market_tab.symbol,
            nse_cash_market_tab.trade_date,
            nse_cash_market_tab.open AS cmopen,
            nse_cash_market_tab.high AS cmhigh,
            nse_cash_market_tab.low AS cmlow,
            nse_cash_market_tab.close AS cmclose,
            nse_cash_market_tab.last AS cmlast,
            nse_cash_market_tab.prev_close AS cmclose_prev,
            nse_cash_market_tab.tot_trd_val,
            nse_cash_market_tab.tot_trd_qty
           FROM public.nse_cash_market_tab) cmt
     JOIN ( SELECT nse_delivery_market_tab.symbol,
            nse_delivery_market_tab.trade_date,
            nse_delivery_market_tab.traded_qty,
            nse_delivery_market_tab.deliverable_qty AS delivered_qty,
            nse_delivery_market_tab.delivery_to_trade_ratio AS del_to_trd_ratio
           FROM public.nse_delivery_market_tab) dmt ON ((((cmt.symbol)::text = (dmt.symbol)::text) AND (cmt.trade_date = dmt.trade_date))))
     LEFT JOIN ( SELECT nfmt.symbol,
            nfmt.trade_date,
            nfmt.expiry_date,
            nfmt.open AS fuopen,
            nfmt.high AS fuhigh,
            nfmt.low AS fulow,
            nfmt.close AS fuclose,
            nfmt.settle_price AS fulast,
            nfmt.open_int AS oi
           FROM public.nse_future_market_tab nfmt,
            public.min_expiry_view1 mev
          WHERE (((nfmt.symbol)::text = (mev.symbol)::text) AND (nfmt.trade_date = mev.trade_date) AND (nfmt.expiry_date = mev.min_expiry_date))) fmt ON ((((cmt.symbol)::text = (fmt.symbol)::text) AND (cmt.trade_date = fmt.trade_date))))
  WITH NO DATA;


ALTER TABLE public.cfd_data_all_mv OWNER TO postgres;

--
-- Name: cfd_data_cd_left_join_f_mv; Type: MATERIALIZED VIEW; Schema: public; Owner: postgres
--

CREATE MATERIALIZED VIEW public.cfd_data_cd_left_join_f_mv AS
 SELECT cmt.symbol,
    to_char((cmt.trade_date)::timestamp with time zone, 'DY'::text) AS cmday,
    cmt.trade_date,
    cmt.cmopen,
    cmt.cmhigh,
    cmt.cmlow,
    cmt.cmclose,
    cmt.cmlast,
    cmt.cmclose_prev,
    round((cmt.tot_trd_val / (cmt.tot_trd_qty)::numeric), 2) AS cmatp,
    dmt.traded_qty,
    dmt.delivered_qty,
    dmt.del_to_trd_ratio,
    cmt.tot_trd_qty,
    cmt.tot_trd_val,
    fmt.fuopen,
    fmt.fuhigh,
    fmt.fulow,
    fmt.fuclose,
    fmt.fulast,
    (fmt.fuclose - cmt.cmclose) AS fupremium,
    fmt.fuoi,
    fmt.fucontracts,
    fmt.fu_tot_trd_val
   FROM ((( SELECT nse_cash_market_tab.symbol,
            nse_cash_market_tab.trade_date,
            nse_cash_market_tab.open AS cmopen,
            nse_cash_market_tab.high AS cmhigh,
            nse_cash_market_tab.low AS cmlow,
            nse_cash_market_tab.close AS cmclose,
            nse_cash_market_tab.last AS cmlast,
            nse_cash_market_tab.prev_close AS cmclose_prev,
            nse_cash_market_tab.tot_trd_val,
            nse_cash_market_tab.tot_trd_qty
           FROM public.nse_cash_market_tab) cmt
     JOIN ( SELECT nse_delivery_market_tab.symbol,
            nse_delivery_market_tab.trade_date,
            nse_delivery_market_tab.traded_qty,
            nse_delivery_market_tab.deliverable_qty AS delivered_qty,
            nse_delivery_market_tab.delivery_to_trade_ratio AS del_to_trd_ratio
           FROM public.nse_delivery_market_tab) dmt ON ((((cmt.symbol)::text = (dmt.symbol)::text) AND (cmt.trade_date = dmt.trade_date))))
     LEFT JOIN ( SELECT nfmt.symbol,
            nfmt.trade_date,
            nfmt.expiry_date,
            nfmt.open AS fuopen,
            nfmt.high AS fuhigh,
            nfmt.low AS fulow,
            nfmt.close AS fuclose,
            nfmt.settle_price AS fulast,
            nfmt.open_int AS fuoi,
            (nfmt.value_in_lakh * (100000)::numeric) AS fu_tot_trd_val,
            nfmt.contracts AS fucontracts
           FROM public.nse_future_market_tab nfmt,
            public.min_expiry_view1 mev
          WHERE (((nfmt.symbol)::text = (mev.symbol)::text) AND (nfmt.trade_date = mev.trade_date) AND (nfmt.expiry_date = mev.min_expiry_date))) fmt ON ((((cmt.symbol)::text = (fmt.symbol)::text) AND (cmt.trade_date = fmt.trade_date))))
  WITH NO DATA;


ALTER TABLE public.cfd_data_cd_left_join_f_mv OWNER TO postgres;

--
-- Name: cfd_data_cd_left_join_f_mv2; Type: MATERIALIZED VIEW; Schema: public; Owner: postgres
--

CREATE MATERIALIZED VIEW public.cfd_data_cd_left_join_f_mv2 AS
 SELECT cmt.symbol,
    to_char((cmt.trade_date)::timestamp with time zone, 'DY'::text) AS cmday,
    cmt.trade_date,
    cmt.cmopen,
    cmt.cmhigh,
    cmt.cmlow,
    cmt.cmclose,
    cmt.cmlast,
    cmt.cmclose_prev,
    round((cmt.tot_trd_val / (cmt.tot_trd_qty)::numeric), 2) AS cmatp,
    dmt.traded_qty,
    dmt.delivered_qty,
    dmt.del_to_trd_ratio,
    cmt.tot_trd_qty,
    cmt.tot_trd_val,
    fmt.fuopen,
    fmt.fuhigh,
    fmt.fulow,
    fmt.fuclose,
    fmt.fulast,
    (fmt.fuclose - cmt.cmclose) AS fupremium,
    fmt.fuoi,
    fmt.fucontracts,
    fmt.fu_tot_trd_val
   FROM ((( SELECT nse_cash_market_tab.symbol,
            nse_cash_market_tab.trade_date,
            nse_cash_market_tab.open AS cmopen,
            nse_cash_market_tab.high AS cmhigh,
            nse_cash_market_tab.low AS cmlow,
            nse_cash_market_tab.close AS cmclose,
            nse_cash_market_tab.last AS cmlast,
            nse_cash_market_tab.prev_close AS cmclose_prev,
            nse_cash_market_tab.tot_trd_val,
            nse_cash_market_tab.tot_trd_qty
           FROM public.nse_cash_market_tab) cmt
     JOIN ( SELECT nse_delivery_market_tab.symbol,
            nse_delivery_market_tab.trade_date,
            nse_delivery_market_tab.traded_qty,
            nse_delivery_market_tab.deliverable_qty AS delivered_qty,
            nse_delivery_market_tab.delivery_to_trade_ratio AS del_to_trd_ratio
           FROM public.nse_delivery_market_tab) dmt ON ((((cmt.symbol)::text = (dmt.symbol)::text) AND (cmt.trade_date = dmt.trade_date))))
     LEFT JOIN ( SELECT nfmt.symbol,
            nfmt.trade_date,
            nfmt.expiry_date,
            nfmt.open AS fuopen,
            nfmt.high AS fuhigh,
            nfmt.low AS fulow,
            nfmt.close AS fuclose,
            nfmt.settle_price AS fulast,
            nfmt.open_int AS fuoi,
            (nfmt.value_in_lakh * (100000)::numeric) AS fu_tot_trd_val,
            nfmt.contracts AS fucontracts
           FROM public.nse_future_market_tab nfmt,
            public.min_expiry_view1 mev
          WHERE (((nfmt.symbol)::text = (mev.symbol)::text) AND (nfmt.trade_date = mev.trade_date) AND (nfmt.expiry_date = mev.min_expiry_date))) fmt ON ((((cmt.symbol)::text = (fmt.symbol)::text) AND (cmt.trade_date = fmt.trade_date))))
  WITH NO DATA;


ALTER TABLE public.cfd_data_cd_left_join_f_mv2 OWNER TO postgres;

--
-- Name: cfd_data_cd_left_join_f_view; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.cfd_data_cd_left_join_f_view AS
 SELECT cmt.symbol,
    cmt.trade_date,
    cmt.cmopen,
    cmt.cmhigh,
    cmt.cmlow,
    cmt.cmclose,
    cmt.cmlast,
    cmt.cmclose_prev,
    round((cmt.tot_trd_val / (cmt.tot_trd_qty)::numeric), 2) AS cmatp,
    dmt.traded_qty,
    dmt.delivered_qty,
    dmt.del_to_trd_ratio,
    cmt.tot_trd_qty,
    cmt.tot_trd_val,
    fmt.fuopen,
    fmt.fuhigh,
    fmt.fulow,
    fmt.fuclose,
    fmt.fulast,
    (fmt.fuclose - cmt.cmclose) AS fupremium,
    fmt.fuoi,
    fmt.fucontracts,
    fmt.fu_tot_trd_val
   FROM ((( SELECT nse_cash_market_tab.symbol,
            nse_cash_market_tab.trade_date,
            nse_cash_market_tab.open AS cmopen,
            nse_cash_market_tab.high AS cmhigh,
            nse_cash_market_tab.low AS cmlow,
            nse_cash_market_tab.close AS cmclose,
            nse_cash_market_tab.last AS cmlast,
            nse_cash_market_tab.prev_close AS cmclose_prev,
            nse_cash_market_tab.tot_trd_val,
            nse_cash_market_tab.tot_trd_qty
           FROM public.nse_cash_market_tab) cmt
     JOIN ( SELECT nse_delivery_market_tab.symbol,
            nse_delivery_market_tab.trade_date,
            nse_delivery_market_tab.traded_qty,
            nse_delivery_market_tab.deliverable_qty AS delivered_qty,
            nse_delivery_market_tab.delivery_to_trade_ratio AS del_to_trd_ratio
           FROM public.nse_delivery_market_tab) dmt ON ((((cmt.symbol)::text = (dmt.symbol)::text) AND (cmt.trade_date = dmt.trade_date))))
     LEFT JOIN ( SELECT nfmt.symbol,
            nfmt.trade_date,
            nfmt.expiry_date,
            nfmt.open AS fuopen,
            nfmt.high AS fuhigh,
            nfmt.low AS fulow,
            nfmt.close AS fuclose,
            nfmt.settle_price AS fulast,
            nfmt.open_int AS fuoi,
            (nfmt.value_in_lakh * (100000)::numeric) AS fu_tot_trd_val,
            nfmt.contracts AS fucontracts
           FROM public.nse_future_market_tab nfmt,
            public.min_expiry_view1 mev
          WHERE (((nfmt.symbol)::text = (mev.symbol)::text) AND (nfmt.trade_date = mev.trade_date) AND (nfmt.expiry_date = mev.min_expiry_date))) fmt ON ((((cmt.symbol)::text = (fmt.symbol)::text) AND (cmt.trade_date = fmt.trade_date))));


ALTER TABLE public.cfd_data_cd_left_join_f_view OWNER TO postgres;

--
-- Name: cfd_data_mv_a1; Type: MATERIALIZED VIEW; Schema: public; Owner: postgres
--

CREATE MATERIALIZED VIEW public.cfd_data_mv_a1 AS
 SELECT cmt.symbol,
    cmt.trade_date,
    cmt.cmopen,
    cmt.cmhigh,
    cmt.cmlow,
    cmt.cmclose,
    cmt.cmlast,
    cmt.cmclose_prev,
    round((cmt.tot_trd_val / (cmt.tot_trd_qty)::numeric), 2) AS cmatp,
    dmt.traded_qty,
    dmt.delivered_qty,
    dmt.del_to_trd_ratio,
    cmt.tot_trd_qty,
    cmt.tot_trd_val,
    fmt.fuopen,
    fmt.fuhigh,
    fmt.fulow,
    fmt.fuclose,
    fmt.fulast,
    (fmt.fuclose - cmt.cmclose) AS premium,
    fmt.oi
   FROM ((( SELECT nse_cash_market_tab.symbol,
            nse_cash_market_tab.trade_date,
            nse_cash_market_tab.open AS cmopen,
            nse_cash_market_tab.high AS cmhigh,
            nse_cash_market_tab.low AS cmlow,
            nse_cash_market_tab.close AS cmclose,
            nse_cash_market_tab.last AS cmlast,
            nse_cash_market_tab.prev_close AS cmclose_prev,
            nse_cash_market_tab.tot_trd_val,
            nse_cash_market_tab.tot_trd_qty
           FROM public.nse_cash_market_tab) cmt
     JOIN ( SELECT nse_delivery_market_tab.symbol,
            nse_delivery_market_tab.trade_date,
            nse_delivery_market_tab.traded_qty,
            nse_delivery_market_tab.deliverable_qty AS delivered_qty,
            nse_delivery_market_tab.delivery_to_trade_ratio AS del_to_trd_ratio
           FROM public.nse_delivery_market_tab
          WHERE ((nse_delivery_market_tab.security_type)::text = 'EQ'::text)) dmt ON ((((cmt.symbol)::text = (dmt.symbol)::text) AND (cmt.trade_date = dmt.trade_date))))
     JOIN ( SELECT fmt_1.symbol,
            fmt_1.trade_date,
            fmt_1.expiry_date,
            fmt_1.open AS fuopen,
            fmt_1.high AS fuhigh,
            fmt_1.low AS fulow,
            fmt_1.close AS fuclose,
            fmt_1.settle_price AS fulast,
            fmt_1.open_int AS oi
           FROM public.nse_future_market_tab fmt_1,
            public.min_expiry_view1 mev
          WHERE (((fmt_1.symbol)::text = (mev.symbol)::text) AND (fmt_1.trade_date = mev.trade_date) AND (fmt_1.expiry_date = mev.min_expiry_date))) fmt ON ((((cmt.symbol)::text = (fmt.symbol)::text) AND (cmt.trade_date = fmt.trade_date))))
  WITH NO DATA;


ALTER TABLE public.cfd_data_mv_a1 OWNER TO postgres;

--
-- Name: cfd_data_view1; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.cfd_data_view1 AS
 SELECT cmt.symbol,
    cmt.trade_date,
    cmt.cmopen,
    cmt.cmhigh,
    cmt.cmlow,
    cmt.cmclose,
    cmt.cmclose_two,
    fmt.fuopen,
    fmt.fuhigh,
    fmt.fulow,
    fmt.fuclose,
    (fmt.fuclose - cmt.cmclose) AS premium,
    dmt.traded_qty,
    dmt.delivered_qty,
    dmt.del_to_trd_ratio
   FROM ((( SELECT nse_cash_market_tab.symbol,
            nse_cash_market_tab.trade_date,
            nse_cash_market_tab.open AS cmopen,
            nse_cash_market_tab.high AS cmhigh,
            nse_cash_market_tab.low AS cmlow,
            nse_cash_market_tab.close AS cmclose,
            nse_cash_market_tab.prev_close AS cmclose_two
           FROM public.nse_cash_market_tab) cmt
     RIGHT JOIN ( SELECT fmt_1.symbol,
            fmt_1.trade_date,
            fmt_1.expiry_date,
            fmt_1.open AS fuopen,
            fmt_1.high AS fuhigh,
            fmt_1.low AS fulow,
            fmt_1.close AS fuclose
           FROM public.nse_future_market_tab fmt_1,
            public.min_expiry_view1 mev
          WHERE (((fmt_1.symbol)::text = (mev.symbol)::text) AND (fmt_1.trade_date = mev.trade_date) AND (fmt_1.expiry_date = mev.min_expiry_date))) fmt ON ((((cmt.symbol)::text = (fmt.symbol)::text) AND (cmt.trade_date = fmt.trade_date))))
     LEFT JOIN ( SELECT nse_delivery_market_tab.symbol,
            nse_delivery_market_tab.trade_date,
            nse_delivery_market_tab.traded_qty,
            nse_delivery_market_tab.deliverable_qty AS delivered_qty,
            nse_delivery_market_tab.delivery_to_trade_ratio AS del_to_trd_ratio
           FROM public.nse_delivery_market_tab
          WHERE ((nse_delivery_market_tab.security_type)::text = 'EQ'::text)) dmt ON ((((cmt.symbol)::text = (dmt.symbol)::text) AND (cmt.trade_date = dmt.trade_date))));


ALTER TABLE public.cfd_data_view1 OWNER TO postgres;

--
-- Name: cfd_data_view_a1; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.cfd_data_view_a1 AS
 SELECT cmt.symbol,
    cmt.trade_date,
    cmt.cmopen,
    cmt.cmhigh,
    cmt.cmlow,
    cmt.cmclose,
    cmt.cmlast,
    cmt.cmclose_prev,
    round((cmt.tot_trd_val / (cmt.tot_trd_qty)::numeric), 2) AS cmatp,
    dmt.traded_qty,
    dmt.delivered_qty,
    dmt.del_to_trd_ratio,
    cmt.tot_trd_qty,
    cmt.tot_trd_val,
    fmt.fuopen,
    fmt.fuhigh,
    fmt.fulow,
    fmt.fuclose,
    fmt.fulast,
    (fmt.fuclose - cmt.cmclose) AS premium,
    fmt.oi
   FROM ((( SELECT nse_cash_market_tab.symbol,
            nse_cash_market_tab.trade_date,
            nse_cash_market_tab.open AS cmopen,
            nse_cash_market_tab.high AS cmhigh,
            nse_cash_market_tab.low AS cmlow,
            nse_cash_market_tab.close AS cmclose,
            nse_cash_market_tab.last AS cmlast,
            nse_cash_market_tab.prev_close AS cmclose_prev,
            nse_cash_market_tab.tot_trd_val,
            nse_cash_market_tab.tot_trd_qty
           FROM public.nse_cash_market_tab) cmt
     JOIN ( SELECT nse_delivery_market_tab.symbol,
            nse_delivery_market_tab.trade_date,
            nse_delivery_market_tab.traded_qty,
            nse_delivery_market_tab.deliverable_qty AS delivered_qty,
            nse_delivery_market_tab.delivery_to_trade_ratio AS del_to_trd_ratio
           FROM public.nse_delivery_market_tab
          WHERE ((nse_delivery_market_tab.security_type)::text = 'EQ'::text)) dmt ON ((((cmt.symbol)::text = (dmt.symbol)::text) AND (cmt.trade_date = dmt.trade_date))))
     JOIN ( SELECT fmt_1.symbol,
            fmt_1.trade_date,
            fmt_1.expiry_date,
            fmt_1.open AS fuopen,
            fmt_1.high AS fuhigh,
            fmt_1.low AS fulow,
            fmt_1.close AS fuclose,
            fmt_1.settle_price AS fulast,
            fmt_1.open_int AS oi
           FROM public.nse_future_market_tab fmt_1,
            public.min_expiry_view1 mev
          WHERE (((fmt_1.symbol)::text = (mev.symbol)::text) AND (fmt_1.trade_date = mev.trade_date) AND (fmt_1.expiry_date = mev.min_expiry_date))) fmt ON ((((cmt.symbol)::text = (fmt.symbol)::text) AND (cmt.trade_date = fmt.trade_date))));


ALTER TABLE public.cfd_data_view_a1 OWNER TO postgres;

--
-- Name: cfd_data_view_a1_full; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.cfd_data_view_a1_full AS
 SELECT cmt.symbol,
    cmt.trade_date,
    cmt.cmopen,
    cmt.cmhigh,
    cmt.cmlow,
    cmt.cmclose,
    cmt.cmlast,
    cmt.cmclose_prev,
    round((cmt.tot_trd_val / (cmt.tot_trd_qty)::numeric), 2) AS cmatp,
    dmt.traded_qty,
    dmt.delivered_qty,
    dmt.del_to_trd_ratio,
    cmt.tot_trd_qty,
    cmt.tot_trd_val,
    fmt.fuopen,
    fmt.fuhigh,
    fmt.fulow,
    fmt.fuclose,
    fmt.fulast,
    (fmt.fuclose - cmt.cmclose) AS premium,
    fmt.oi
   FROM ((( SELECT nse_cash_market_tab.symbol,
            nse_cash_market_tab.trade_date,
            nse_cash_market_tab.open AS cmopen,
            nse_cash_market_tab.high AS cmhigh,
            nse_cash_market_tab.low AS cmlow,
            nse_cash_market_tab.close AS cmclose,
            nse_cash_market_tab.last AS cmlast,
            nse_cash_market_tab.prev_close AS cmclose_prev,
            nse_cash_market_tab.tot_trd_val,
            nse_cash_market_tab.tot_trd_qty
           FROM public.nse_cash_market_tab) cmt
     JOIN ( SELECT nse_delivery_market_tab.symbol,
            nse_delivery_market_tab.trade_date,
            nse_delivery_market_tab.traded_qty,
            nse_delivery_market_tab.deliverable_qty AS delivered_qty,
            nse_delivery_market_tab.delivery_to_trade_ratio AS del_to_trd_ratio
           FROM public.nse_delivery_market_tab
          WHERE ((nse_delivery_market_tab.security_type)::text = 'EQ'::text)) dmt ON ((((cmt.symbol)::text = (dmt.symbol)::text) AND (cmt.trade_date = dmt.trade_date))))
     LEFT JOIN ( SELECT fmt_1.symbol,
            fmt_1.trade_date,
            fmt_1.expiry_date,
            fmt_1.open AS fuopen,
            fmt_1.high AS fuhigh,
            fmt_1.low AS fulow,
            fmt_1.close AS fuclose,
            fmt_1.settle_price AS fulast,
            fmt_1.open_int AS oi
           FROM public.nse_future_market_tab fmt_1,
            public.min_expiry_view1 mev
          WHERE (((fmt_1.symbol)::text = (mev.symbol)::text) AND (fmt_1.trade_date = mev.trade_date) AND (fmt_1.expiry_date = mev.min_expiry_date))) fmt ON ((((cmt.symbol)::text = (fmt.symbol)::text) AND (cmt.trade_date = fmt.trade_date))));


ALTER TABLE public.cfd_data_view_a1_full OWNER TO postgres;

--
-- Name: cm_trade_date_ranking_mv; Type: MATERIALIZED VIEW; Schema: public; Owner: postgres
--

CREATE MATERIALIZED VIEW public.cm_trade_date_ranking_mv AS
 SELECT tt1.trade_date,
    rank() OVER (ORDER BY tt1.trade_date DESC) AS rank_trade_day
   FROM ( SELECT DISTINCT nse_cash_market_tab.trade_date
           FROM public.nse_cash_market_tab) tt1
  WITH NO DATA;


ALTER TABLE public.cm_trade_date_ranking_mv OWNER TO postgres;

--
-- Name: cm_date_linking_view1; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.cm_date_linking_view1 AS
 SELECT v1.trade_date AS one_trade_date,
    v1.rank_trade_day AS one_rank,
    v2.trade_date AS two_trade_date,
    v2.rank_trade_day AS two_rank,
    v3.trade_date AS three_trade_date,
    v3.rank_trade_day AS three_rank,
    v4.trade_date AS four_trade_date,
    v4.rank_trade_day AS four_rank,
    v5.trade_date AS five_trade_date,
    v5.rank_trade_day AS five_rank
   FROM public.cm_trade_date_ranking_mv v1,
    public.cm_trade_date_ranking_mv v2,
    public.cm_trade_date_ranking_mv v3,
    public.cm_trade_date_ranking_mv v4,
    public.cm_trade_date_ranking_mv v5
  WHERE (((v1.rank_trade_day + 1) = v2.rank_trade_day) AND ((v2.rank_trade_day + 1) = v3.rank_trade_day) AND ((v3.rank_trade_day + 1) = v4.rank_trade_day) AND ((v4.rank_trade_day + 1) = v5.rank_trade_day));


ALTER TABLE public.cm_date_linking_view1 OWNER TO postgres;

--
-- Name: trade_date_order_view1; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.trade_date_order_view1 AS
 SELECT tt1.trade_date,
    rank() OVER (ORDER BY tt1.trade_date DESC) AS rank_trade_day
   FROM ( SELECT DISTINCT nse_future_market_tab.trade_date
           FROM public.nse_future_market_tab) tt1;


ALTER TABLE public.trade_date_order_view1 OWNER TO postgres;

--
-- Name: date_linking_view1; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.date_linking_view1 AS
 SELECT v1.trade_date AS one_trade_date,
    v1.rank_trade_day AS one_rank,
    v2.trade_date AS two_trade_date,
    v2.rank_trade_day AS two_rank,
    v3.trade_date AS three_trade_date,
    v3.rank_trade_day AS three_rank,
    v4.trade_date AS four_trade_date,
    v4.rank_trade_day AS four_rank,
    v5.trade_date AS five_trade_date,
    v5.rank_trade_day AS five_rank
   FROM public.trade_date_order_view1 v1,
    public.trade_date_order_view1 v2,
    public.trade_date_order_view1 v3,
    public.trade_date_order_view1 v4,
    public.trade_date_order_view1 v5
  WHERE (((v1.rank_trade_day + 1) = v2.rank_trade_day) AND ((v2.rank_trade_day + 1) = v3.rank_trade_day) AND ((v3.rank_trade_day + 1) = v4.rank_trade_day) AND ((v4.rank_trade_day + 1) = v5.rank_trade_day));


ALTER TABLE public.date_linking_view1 OWNER TO postgres;

--
-- Name: daily_spike_view1; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.daily_spike_view1 AS
 SELECT tdy.one_rank AS tdy_rank,
    tdy.trade_date AS tdy_trd_date,
    yes.one_rank AS yes_rank,
    yes.trade_date AS yes_trd_date,
    tdy.symbol AS tdy_symbol,
    yes.symbol AS yes_symbol,
    tdy.cmclose AS tdy_close,
    yes.cmclose AS yes_close,
    (tdy.cmclose - yes.cmclose) AS close_chg,
    (round((tdy.cmclose / (yes.cmclose / (100)::numeric)), 2) - (100)::numeric) AS close_chg_prcnt,
    tdy.cmatp AS tdy_atp,
    yes.cmatp AS yes_atp,
    (tdy.cmatp - yes.cmatp) AS atp_chg,
    (round((tdy.cmatp / (yes.cmatp / (100)::numeric)), 2) - (100)::numeric) AS atp_chg_prcnt,
    tdy.premium AS tdy_premium,
    yes.premium AS yes_premium,
    (tdy.premium - yes.premium) AS premium_chg,
    (round((tdy.premium /
        CASE
            WHEN ((yes.premium / (100)::numeric) = (0)::numeric) THEN (1)::numeric
            ELSE (yes.premium / (100)::numeric)
        END), 2) - (100)::numeric) AS premium_chg_prcnt,
    tdy.oi AS tdy_oi,
    yes.oi AS yes_oi,
    (tdy.oi - yes.oi) AS oi_chg,
    (yes.oi / 100) AS oii,
    (tdy.oi / (yes.oi / 100)) AS oi_chg_prcnt0,
    ((tdy.oi / (yes.oi / 100)) - 100) AS oi_chg_prcnt1,
    (round(((tdy.oi / (yes.oi / 100)))::numeric, 2) - (100)::numeric) AS oi_chg_prcnt,
    tdy.traded_qty AS tdy_traded,
    yes.traded_qty AS yes_traded,
    (tdy.traded_qty - yes.traded_qty) AS traded_chg,
    (round(((tdy.traded_qty / (yes.traded_qty / 100)))::numeric, 2) - (100)::numeric) AS traded_chg_prcnt,
    tdy.delivered_qty AS tdy_delivered,
    yes.delivered_qty AS yes_delivered,
    (tdy.delivered_qty - yes.delivered_qty) AS delivery_chg,
    (round(((tdy.delivered_qty / (yes.delivered_qty / 100)))::numeric, 2) - (100)::numeric) AS delivered_chg_prcnt,
    tdy.cmhigh AS tdyhigh,
    (tdy.cmhigh - yes.cmopen) AS hmo,
    (round((tdy.cmhigh / (yes.cmopen / (100)::numeric)), 2) - (100)::numeric) AS hmo_prcnt,
    tdy.cmopen AS tdyopen,
    tdy.del_to_trd_ratio AS tdy_del_to_trd_ratio,
    yes.del_to_trd_ratio AS yes_del_to_trd_ratio
   FROM ( SELECT v1.one_trade_date,
            v1.one_rank,
            v1.two_trade_date,
            v1.two_rank,
            v1.three_trade_date,
            v1.three_rank,
            v1.four_trade_date,
            v1.four_rank,
            v1.five_trade_date,
            v1.five_rank,
            d1.symbol,
            d1.trade_date,
            d1.cmopen,
            d1.cmhigh,
            d1.cmlow,
            d1.cmclose,
            d1.cmlast,
            d1.cmclose_prev,
            d1.cmatp,
            d1.fuopen,
            d1.fuhigh,
            d1.fulow,
            d1.fuclose,
            d1.fulast,
            d1.premium,
            d1.oi,
            d1.traded_qty,
            d1.delivered_qty,
            d1.del_to_trd_ratio,
            d1.tot_trd_qty,
            d1.tot_trd_val
           FROM public.date_linking_view1 v1,
            public.cfd_data_view_a1 d1
          WHERE (v1.one_trade_date = d1.trade_date)) tdy,
    ( SELECT v2.one_trade_date,
            v2.one_rank,
            v2.two_trade_date,
            v2.two_rank,
            v2.three_trade_date,
            v2.three_rank,
            v2.four_trade_date,
            v2.four_rank,
            v2.five_trade_date,
            v2.five_rank,
            d2.symbol,
            d2.trade_date,
            d2.cmopen,
            d2.cmhigh,
            d2.cmlow,
            d2.cmclose,
            d2.cmlast,
            d2.cmclose_prev,
            d2.cmatp,
            d2.fuopen,
            d2.fuhigh,
            d2.fulow,
            d2.fuclose,
            d2.fulast,
            d2.premium,
            d2.oi,
            d2.traded_qty,
            d2.delivered_qty,
            d2.del_to_trd_ratio,
            d2.tot_trd_qty,
            d2.tot_trd_val
           FROM public.date_linking_view1 v2,
            public.cfd_data_view_a1 d2
          WHERE (v2.two_trade_date = d2.trade_date)) yes
  WHERE ((tdy.one_trade_date = yes.one_trade_date) AND ((tdy.symbol)::text = (yes.symbol)::text))
  ORDER BY tdy.symbol, tdy.one_trade_date;


ALTER TABLE public.daily_spike_view1 OWNER TO postgres;

--
-- Name: distinct_future_trade_date_view; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.distinct_future_trade_date_view AS
 SELECT ranked_trade_date.trade_date,
    ranked_trade_date.expiry_date,
    ranked_trade_date.trade_date_rank,
    row_number() OVER (PARTITION BY ranked_trade_date.trade_date ORDER BY ranked_trade_date.expiry_date) AS expiry_date_rank
   FROM ( SELECT dt.trade_date,
            dt.expiry_date,
            rank() OVER (ORDER BY dt.trade_date DESC) AS trade_date_rank
           FROM ( SELECT DISTINCT nse_future_market_tab.trade_date,
                    nse_future_market_tab.expiry_date
                   FROM public.nse_future_market_tab
                  WHERE ((nse_future_market_tab.instrument)::text = 'FUTSTK'::text)) dt) ranked_trade_date;


ALTER TABLE public.distinct_future_trade_date_view OWNER TO postgres;

--
-- Name: fm_lots; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.fm_lots (
    symbol text,
    size text
);


ALTER TABLE public.fm_lots OWNER TO postgres;

--
-- Name: fut_data_view1; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.fut_data_view1 AS
 SELECT tab1.symbol,
    tab1.instrument,
    tab1.trade_date,
    vew1.trade_date_rank,
    tab1.expiry_date,
    vew1.expiry_date_rank,
    tab1.close AS fu_close,
    tab1.open_int AS oi
   FROM public.nse_future_market_tab tab1,
    public.distinct_future_trade_date_view vew1
  WHERE (((tab1.instrument)::text = 'FUTSTK'::text) AND (tab1.trade_date = vew1.trade_date) AND (tab1.expiry_date = vew1.expiry_date))
  ORDER BY tab1.symbol, tab1.trade_date, tab1.expiry_date;


ALTER TABLE public.fut_data_view1 OWNER TO postgres;

--
-- Name: min_expiry_mv; Type: MATERIALIZED VIEW; Schema: public; Owner: postgres
--

CREATE MATERIALIZED VIEW public.min_expiry_mv AS
 SELECT nse_future_market_tab.symbol,
    nse_future_market_tab.trade_date,
    min(nse_future_market_tab.expiry_date) AS min_expiry_date
   FROM public.nse_future_market_tab
  GROUP BY nse_future_market_tab.symbol, nse_future_market_tab.trade_date
  WITH NO DATA;


ALTER TABLE public.min_expiry_mv OWNER TO postgres;

--
-- Name: nse_index_market_seq_id; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.nse_index_market_seq_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.nse_index_market_seq_id OWNER TO postgres;

--
-- Name: nse_index_market_tab; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.nse_index_market_tab (
    id bigint DEFAULT nextval('public.nse_index_market_seq_id'::regclass) NOT NULL,
    symbol character varying(32) NOT NULL,
    idx_name character varying(32),
    trade_date date,
    open numeric(18,2),
    high numeric(18,2),
    low numeric(18,2),
    close numeric(18,2),
    points_chg_abs numeric(18,2),
    points_chg_pct numeric(18,2),
    volume bigint,
    turn_over_in_crore numeric(18,2),
    pe numeric(18,2),
    pb numeric(18,2),
    div_yield numeric(18,2)
);


ALTER TABLE public.nse_index_market_tab OWNER TO postgres;

--
-- Name: nse_option_market_seq_id; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.nse_option_market_seq_id
    START WITH 1089705
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.nse_option_market_seq_id OWNER TO postgres;

--
-- Name: nse_option_market_tab; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.nse_option_market_tab (
    id bigint DEFAULT nextval('public.nse_option_market_seq_id'::regclass) NOT NULL,
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
    trade_date date
);


ALTER TABLE public.nse_option_market_tab OWNER TO postgres;

--
-- Name: pivot_oi_view1; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.pivot_oi_view1 AS
 SELECT source.symbol,
    source.instrument,
    source.trade_date,
    source.trade_date_rank,
    sum(source.oi_one) AS oi_one,
    sum(source.oi_two) AS oi_two,
    sum(source.oi_three) AS oi_three
   FROM ( SELECT fut_data_view1.symbol,
            fut_data_view1.instrument,
            fut_data_view1.trade_date,
            fut_data_view1.trade_date_rank,
            fut_data_view1.expiry_date,
            fut_data_view1.expiry_date_rank,
            fut_data_view1.fu_close,
            fut_data_view1.oi,
                CASE
                    WHEN (fut_data_view1.expiry_date_rank = 1) THEN fut_data_view1.oi
                    ELSE NULL::bigint
                END AS oi_one,
                CASE
                    WHEN (fut_data_view1.expiry_date_rank = 2) THEN fut_data_view1.oi
                    ELSE NULL::bigint
                END AS oi_two,
                CASE
                    WHEN (fut_data_view1.expiry_date_rank = 3) THEN fut_data_view1.oi
                    ELSE NULL::bigint
                END AS oi_three
           FROM public.fut_data_view1
          WHERE (fut_data_view1.trade_date_rank = 1)) source
  GROUP BY source.symbol, source.instrument, source.trade_date, source.trade_date_rank;


ALTER TABLE public.pivot_oi_view1 OWNER TO postgres;

--
-- Name: three_day_fall; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.three_day_fall AS
 SELECT t1.symbol,
    t1.one_rank,
    t1.one_trade_date AS trade_date,
    t1.one_trade_date AS trade_date1,
    t2.two_trade_date AS trade_date2,
    t3.three_trade_date AS trade_date3,
    t1.cmclose AS close1,
    t2.cmclose AS close2,
    t3.cmclose AS close3,
    t1.cmclose_prev AS previous_close,
    t1.cmopen AS open,
    t1.cmhigh AS high,
    t1.cmlow AS low,
    t1.cmclose AS close,
    t1.cmlast AS last,
    t1.cmatp AS atp,
    (round((t1.cmopen / (t2.cmopen / (100)::numeric)), 2) - (100)::numeric) AS open_chg_prcnt,
    (round((t1.cmhigh / (t2.cmhigh / (100)::numeric)), 2) - (100)::numeric) AS high_chg_prcnt,
    (round((t1.cmlow / (t2.cmlow / (100)::numeric)), 2) - (100)::numeric) AS low_chg_prcnt,
    (round((t1.cmclose / (t2.cmclose / (100)::numeric)), 2) - (100)::numeric) AS close_chg_prcnt,
    (round((t1.cmlast / (t2.cmlast / (100)::numeric)), 2) - (100)::numeric) AS last_chg_prcnt,
    (round((t1.cmatp / (t2.cmatp / (100)::numeric)), 2) - (100)::numeric) AS atp_chg_prcnt,
        CASE
            WHEN ((t2.traded_qty / 100) = 0) THEN (0)::numeric
            ELSE (round(((t1.traded_qty / (t2.traded_qty / 100)))::numeric, 2) - (100)::numeric)
        END AS volume_chg_prcnt,
        CASE
            WHEN ((t2.delivered_qty / 100) = 0) THEN (0)::numeric
            ELSE (round(((t1.delivered_qty / (t2.delivered_qty / 100)))::numeric, 2) - (100)::numeric)
        END AS delivery_chg_prcnt,
    (round((t1.cmopen / (t1.cmclose_prev / (100)::numeric)), 2) - (100)::numeric) AS close_to_open_percent,
    (round((t1.cmhigh / (t1.cmopen / (100)::numeric)), 2) - (100)::numeric) AS othigh_prcnt,
    (round((t1.cmlow / (t1.cmopen / (100)::numeric)), 2) - (100)::numeric) AS otlow_prcnt,
    (round((t1.cmclose / (t1.cmopen / (100)::numeric)), 2) - (100)::numeric) AS otclose_prcnt,
    (round((t1.cmlast / (t1.cmopen / (100)::numeric)), 2) - (100)::numeric) AS otlast_prcnt,
    (round((t1.cmatp / (t1.cmopen / (100)::numeric)), 2) - (100)::numeric) AS otatp_prcnt,
    (t1.cmclose - t2.cmclose) AS t1close_minus_t2close,
    (t1.cmlast - t2.cmlast) AS t1last_minus_t2last,
    (t1.cmatp - t2.cmatp) AS t1atp_minus_t2atp,
    (t1.delivered_qty - t2.delivered_qty) AS t1del_minus_t2del,
    t1.traded_qty AS volume,
    t1.delivered_qty AS delivery,
    t1.fucontracts,
    t1.fu_tot_trd_val
   FROM ( SELECT nse_future_market_tab.symbol,
            min(nse_future_market_tab.trade_date) AS fut_traded_from_date,
            max(nse_future_market_tab.trade_date) AS fut_traded_to_date
           FROM public.nse_future_market_tab
          GROUP BY nse_future_market_tab.symbol) distinct_future_stocks,
    ( SELECT d1.symbol,
            d1.trade_date,
            d1.cmopen,
            d1.cmhigh,
            d1.cmlow,
            d1.cmclose,
            d1.cmlast,
            d1.cmclose_prev,
            d1.cmatp,
            d1.traded_qty,
            d1.delivered_qty,
            d1.del_to_trd_ratio,
            d1.tot_trd_qty,
            d1.tot_trd_val,
            d1.fuopen,
            d1.fuhigh,
            d1.fulow,
            d1.fuclose,
            d1.fulast,
            d1.fupremium,
            d1.fuoi,
            d1.fucontracts,
            d1.fu_tot_trd_val,
            v1.one_trade_date,
            v1.one_rank,
            v1.two_trade_date,
            v1.two_rank,
            v1.three_trade_date,
            v1.three_rank,
            v1.four_trade_date,
            v1.four_rank,
            v1.five_trade_date,
            v1.five_rank,
            v1.one_trade_date AS td1
           FROM public.cfd_data_cd_left_join_f_mv2 d1,
            public.cm_date_linking_view1 v1
          WHERE (d1.trade_date = v1.one_trade_date)) t1,
    ( SELECT d2.symbol,
            d2.trade_date,
            d2.cmopen,
            d2.cmhigh,
            d2.cmlow,
            d2.cmclose,
            d2.cmlast,
            d2.cmclose_prev,
            d2.cmatp,
            d2.traded_qty,
            d2.delivered_qty,
            d2.del_to_trd_ratio,
            d2.tot_trd_qty,
            d2.tot_trd_val,
            d2.fuopen,
            d2.fuhigh,
            d2.fulow,
            d2.fuclose,
            d2.fulast,
            d2.fupremium,
            d2.fuoi,
            d2.fucontracts,
            d2.fu_tot_trd_val,
            v2.one_trade_date,
            v2.one_rank,
            v2.two_trade_date,
            v2.two_rank,
            v2.three_trade_date,
            v2.three_rank,
            v2.four_trade_date,
            v2.four_rank,
            v2.five_trade_date,
            v2.five_rank
           FROM public.cfd_data_cd_left_join_f_mv2 d2,
            public.cm_date_linking_view1 v2
          WHERE (d2.trade_date = v2.two_trade_date)) t2,
    ( SELECT d3.symbol,
            d3.trade_date,
            d3.cmopen,
            d3.cmhigh,
            d3.cmlow,
            d3.cmclose,
            d3.cmlast,
            d3.cmclose_prev,
            d3.cmatp,
            d3.traded_qty,
            d3.delivered_qty,
            d3.del_to_trd_ratio,
            d3.tot_trd_qty,
            d3.tot_trd_val,
            d3.fuopen,
            d3.fuhigh,
            d3.fulow,
            d3.fuclose,
            d3.fulast,
            d3.fupremium,
            d3.fuoi,
            d3.fucontracts,
            d3.fu_tot_trd_val,
            v3.one_trade_date,
            v3.one_rank,
            v3.two_trade_date,
            v3.two_rank,
            v3.three_trade_date,
            v3.three_rank,
            v3.four_trade_date,
            v3.four_rank,
            v3.five_trade_date,
            v3.five_rank
           FROM public.cfd_data_cd_left_join_f_mv2 d3,
            public.cm_date_linking_view1 v3
          WHERE (d3.trade_date = v3.three_trade_date)) t3
  WHERE (((distinct_future_stocks.symbol)::text = (t1.symbol)::text) AND (t1.one_trade_date = t2.one_trade_date) AND ((t1.symbol)::text = (t2.symbol)::text) AND (t1.one_rank = 1) AND (t1.one_trade_date = t3.one_trade_date) AND ((t1.symbol)::text = (t3.symbol)::text) AND (t1.cmclose < t2.cmclose) AND (t2.cmclose < t3.cmclose) AND (t1.cmopen < t2.cmopen) AND (t2.cmopen < t3.cmopen))
  ORDER BY t1.symbol, t1.trade_date;


ALTER TABLE public.three_day_fall OWNER TO postgres;

--
-- Name: three_day_rise; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.three_day_rise AS
 SELECT t1.symbol,
    t1.one_trade_date AS trade_date,
    t1.one_trade_date AS trade_date1,
    t2.two_trade_date AS trade_date2,
    t3.three_trade_date AS trade_date3,
    t1.cmclose AS close1,
    t2.cmclose AS close2,
    t3.cmclose AS close3,
    t1.cmclose_prev AS previous_close,
    t1.cmopen AS open,
    t1.cmhigh AS high,
    t1.cmlow AS low,
    t1.cmclose AS close,
    t1.cmlast AS last,
    t1.cmatp AS atp,
    (round((t1.cmopen / (t2.cmopen / (100)::numeric)), 2) - (100)::numeric) AS open_chg_prcnt,
    (round((t1.cmhigh / (t2.cmhigh / (100)::numeric)), 2) - (100)::numeric) AS high_chg_prcnt,
    (round((t1.cmlow / (t2.cmlow / (100)::numeric)), 2) - (100)::numeric) AS low_chg_prcnt,
    (round((t1.cmclose / (t2.cmclose / (100)::numeric)), 2) - (100)::numeric) AS close_chg_prcnt,
    (round((t1.cmlast / (t2.cmlast / (100)::numeric)), 2) - (100)::numeric) AS last_chg_prcnt,
    (round((t1.cmatp / (t2.cmatp / (100)::numeric)), 2) - (100)::numeric) AS atp_chg_prcnt,
        CASE
            WHEN ((t2.traded_qty / 100) = 0) THEN (0)::numeric
            ELSE (round(((t1.traded_qty / (t2.traded_qty / 100)))::numeric, 2) - (100)::numeric)
        END AS volume_chg_prcnt,
        CASE
            WHEN ((t2.delivered_qty / 100) = 0) THEN (0)::numeric
            ELSE (round(((t1.delivered_qty / (t2.delivered_qty / 100)))::numeric, 2) - (100)::numeric)
        END AS delivery_chg_prcnt,
    (round((t1.cmopen / (t1.cmclose_prev / (100)::numeric)), 2) - (100)::numeric) AS close_to_open_percent,
    (round((t1.cmhigh / (t1.cmopen / (100)::numeric)), 2) - (100)::numeric) AS othigh_prcnt,
    (round((t1.cmlow / (t1.cmopen / (100)::numeric)), 2) - (100)::numeric) AS otlow_prcnt,
    (round((t1.cmclose / (t1.cmopen / (100)::numeric)), 2) - (100)::numeric) AS otclose_prcnt,
    (round((t1.cmlast / (t1.cmopen / (100)::numeric)), 2) - (100)::numeric) AS otlast_prcnt,
    (round((t1.cmatp / (t1.cmopen / (100)::numeric)), 2) - (100)::numeric) AS otatp_prcnt,
    (t1.cmclose - t2.cmclose) AS t1close_minus_t2close,
    (t1.cmlast - t2.cmlast) AS t1last_minus_t2last,
    (t1.cmatp - t2.cmatp) AS t1atp_minus_t2atp,
    (t1.delivered_qty - t2.delivered_qty) AS t1del_minus_t2del,
    t1.traded_qty AS volume,
    t1.delivered_qty AS delivery,
    t1.fucontracts,
    t1.fu_tot_trd_val
   FROM ( SELECT nse_future_market_tab.symbol,
            min(nse_future_market_tab.trade_date) AS fut_traded_from_date,
            max(nse_future_market_tab.trade_date) AS fut_traded_to_date
           FROM public.nse_future_market_tab
          GROUP BY nse_future_market_tab.symbol) distinct_future_stocks,
    ( SELECT d1.symbol,
            d1.trade_date,
            d1.cmopen,
            d1.cmhigh,
            d1.cmlow,
            d1.cmclose,
            d1.cmlast,
            d1.cmclose_prev,
            d1.cmatp,
            d1.traded_qty,
            d1.delivered_qty,
            d1.del_to_trd_ratio,
            d1.tot_trd_qty,
            d1.tot_trd_val,
            d1.fuopen,
            d1.fuhigh,
            d1.fulow,
            d1.fuclose,
            d1.fulast,
            d1.fupremium,
            d1.fuoi,
            d1.fucontracts,
            d1.fu_tot_trd_val,
            v1.one_trade_date,
            v1.one_rank,
            v1.two_trade_date,
            v1.two_rank,
            v1.three_trade_date,
            v1.three_rank,
            v1.four_trade_date,
            v1.four_rank,
            v1.five_trade_date,
            v1.five_rank,
            v1.one_trade_date AS td1
           FROM public.cfd_data_cd_left_join_f_mv2 d1,
            public.cm_date_linking_view1 v1
          WHERE (d1.trade_date = v1.one_trade_date)) t1,
    ( SELECT d2.symbol,
            d2.trade_date,
            d2.cmopen,
            d2.cmhigh,
            d2.cmlow,
            d2.cmclose,
            d2.cmlast,
            d2.cmclose_prev,
            d2.cmatp,
            d2.traded_qty,
            d2.delivered_qty,
            d2.del_to_trd_ratio,
            d2.tot_trd_qty,
            d2.tot_trd_val,
            d2.fuopen,
            d2.fuhigh,
            d2.fulow,
            d2.fuclose,
            d2.fulast,
            d2.fupremium,
            d2.fuoi,
            d2.fucontracts,
            d2.fu_tot_trd_val,
            v2.one_trade_date,
            v2.one_rank,
            v2.two_trade_date,
            v2.two_rank,
            v2.three_trade_date,
            v2.three_rank,
            v2.four_trade_date,
            v2.four_rank,
            v2.five_trade_date,
            v2.five_rank
           FROM public.cfd_data_cd_left_join_f_mv2 d2,
            public.cm_date_linking_view1 v2
          WHERE (d2.trade_date = v2.two_trade_date)) t2,
    ( SELECT d3.symbol,
            d3.trade_date,
            d3.cmopen,
            d3.cmhigh,
            d3.cmlow,
            d3.cmclose,
            d3.cmlast,
            d3.cmclose_prev,
            d3.cmatp,
            d3.traded_qty,
            d3.delivered_qty,
            d3.del_to_trd_ratio,
            d3.tot_trd_qty,
            d3.tot_trd_val,
            d3.fuopen,
            d3.fuhigh,
            d3.fulow,
            d3.fuclose,
            d3.fulast,
            d3.fupremium,
            d3.fuoi,
            d3.fucontracts,
            d3.fu_tot_trd_val,
            v3.one_trade_date,
            v3.one_rank,
            v3.two_trade_date,
            v3.two_rank,
            v3.three_trade_date,
            v3.three_rank,
            v3.four_trade_date,
            v3.four_rank,
            v3.five_trade_date,
            v3.five_rank
           FROM public.cfd_data_cd_left_join_f_mv2 d3,
            public.cm_date_linking_view1 v3
          WHERE (d3.trade_date = v3.three_trade_date)) t3
  WHERE (((distinct_future_stocks.symbol)::text = (t1.symbol)::text) AND (t1.one_trade_date = t2.one_trade_date) AND ((t1.symbol)::text = (t2.symbol)::text) AND (t1.one_rank = 1) AND (t1.one_trade_date = t3.one_trade_date) AND ((t1.symbol)::text = (t3.symbol)::text) AND (t1.cmclose > t2.cmclose) AND (t2.cmclose > t3.cmclose) AND (t1.cmopen > t2.cmopen) AND (t2.cmopen > t3.cmopen))
  ORDER BY t1.symbol, t1.trade_date;


ALTER TABLE public.three_day_rise OWNER TO postgres;

--
-- Name: calc_avg_tab_new calc_avg_unique_new; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.calc_avg_tab_new
    ADD CONSTRAINT calc_avg_unique_new UNIQUE (symbol, trade_date, for_days);


--
-- Name: calc_mfi_tab_new calc_mfi_unique_new; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.calc_mfi_tab_new
    ADD CONSTRAINT calc_mfi_unique_new UNIQUE (symbol, trade_date, for_days);


--
-- Name: calc_rsi_tab_new calc_rsi_unique_new; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.calc_rsi_tab_new
    ADD CONSTRAINT calc_rsi_unique_new UNIQUE (symbol, trade_date, for_days);


--
-- Name: nse_cash_market_tab nse_cash_market_constraint_unique_colums; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nse_cash_market_tab
    ADD CONSTRAINT nse_cash_market_constraint_unique_colums UNIQUE (trade_date, symbol);


--
-- Name: nse_cash_market_tab nse_cash_market_tab_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nse_cash_market_tab
    ADD CONSTRAINT nse_cash_market_tab_pkey PRIMARY KEY (id);


--
-- Name: nse_delivery_market_tab nse_delivery_market_constraint_unique_colums; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nse_delivery_market_tab
    ADD CONSTRAINT nse_delivery_market_constraint_unique_colums UNIQUE (trade_date, symbol, security_type);


--
-- Name: nse_delivery_market_tab nse_delivery_market_tab_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nse_delivery_market_tab
    ADD CONSTRAINT nse_delivery_market_tab_pkey PRIMARY KEY (id);


--
-- Name: nse_future_market_tab nse_future_market_constraint_unique_colums; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nse_future_market_tab
    ADD CONSTRAINT nse_future_market_constraint_unique_colums UNIQUE (trade_date, expiry_date, symbol, instrument, strike_price, option_type);


--
-- Name: nse_future_market_tab nse_future_market_tab_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nse_future_market_tab
    ADD CONSTRAINT nse_future_market_tab_pkey PRIMARY KEY (id);


--
-- Name: nse_index_market_tab nse_index_market_constraint_unique_colums; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nse_index_market_tab
    ADD CONSTRAINT nse_index_market_constraint_unique_colums UNIQUE (trade_date, symbol);


--
-- Name: nse_index_market_tab nse_index_market_tab_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nse_index_market_tab
    ADD CONSTRAINT nse_index_market_tab_pkey PRIMARY KEY (id);


--
-- Name: nse_option_market_tab nse_option_market_constraint_unique_colums; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nse_option_market_tab
    ADD CONSTRAINT nse_option_market_constraint_unique_colums UNIQUE (trade_date, expiry_date, symbol, instrument, strike_price, option_type);


--
-- Name: nse_option_market_tab nse_option_market_tab_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nse_option_market_tab
    ADD CONSTRAINT nse_option_market_tab_pkey PRIMARY KEY (id);


--
-- Name: dm_security_type_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX dm_security_type_idx ON public.nse_delivery_market_tab USING btree (security_type);


--
-- Name: fm_instrument_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX fm_instrument_idx ON public.nse_future_market_tab USING btree (instrument);


--
-- Name: fm_symbol_trade_date_expiry_date_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX fm_symbol_trade_date_expiry_date_idx ON public.nse_future_market_tab USING btree (symbol, trade_date, expiry_date) INCLUDE (symbol, expiry_date, trade_date);


--
-- Name: fm_trade_date_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX fm_trade_date_idx ON public.nse_future_market_tab USING btree (trade_date DESC NULLS LAST);


--
-- Name: fm_trade_date_idx2; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX fm_trade_date_idx2 ON public.nse_future_market_tab USING btree (trade_date DESC NULLS LAST) INCLUDE (trade_date);


--
-- Name: fm_trade_date_symbol_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX fm_trade_date_symbol_idx ON public.nse_future_market_tab USING btree (trade_date DESC NULLS LAST, symbol) INCLUDE (symbol, trade_date);


--
-- Name: nse_cash_market_composite_idx1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX nse_cash_market_composite_idx1 ON public.nse_cash_market_tab USING btree (trade_date, symbol);


--
-- Name: nse_cash_market_pk_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX nse_cash_market_pk_idx ON public.nse_cash_market_tab USING hash (id);


--
-- Name: nse_delivery_market_composite_idx1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX nse_delivery_market_composite_idx1 ON public.nse_delivery_market_tab USING btree (trade_date, security_type, symbol);


--
-- Name: nse_delivery_market_pk_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX nse_delivery_market_pk_idx ON public.nse_delivery_market_tab USING hash (id);


--
-- Name: nse_future_market_composite_idx1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX nse_future_market_composite_idx1 ON public.nse_future_market_tab USING btree (trade_date, expiry_date, instrument, option_type, symbol, strike_price);


--
-- Name: nse_future_market_pk_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX nse_future_market_pk_idx ON public.nse_future_market_tab USING hash (id);


--
-- Name: nse_index_market_composite_idx1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX nse_index_market_composite_idx1 ON public.nse_index_market_tab USING btree (trade_date, symbol);


--
-- Name: nse_index_market_pk_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX nse_index_market_pk_idx ON public.nse_index_market_tab USING hash (id);


--
-- Name: nse_option_market_composite_idx1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX nse_option_market_composite_idx1 ON public.nse_option_market_tab USING btree (trade_date, expiry_date, instrument, option_type, symbol, strike_price);


--
-- Name: nse_option_market_pk_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX nse_option_market_pk_idx ON public.nse_option_market_tab USING hash (id);


--
-- PostgreSQL database dump complete
--

