CREATE VIEW public.min_expiry_view1 AS
 SELECT nse_future_market_tab.symbol,
    nse_future_market_tab.trade_date,
    min(nse_future_market_tab.expiry_date) AS min_expiry_date
   FROM public.nse_future_market_tab
  GROUP BY nse_future_market_tab.symbol, nse_future_market_tab.trade_date;

ALTER TABLE public.min_expiry_view1 OWNER TO postgres;


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


CREATE VIEW public.trade_date_order_view1 AS
 SELECT tt1.trade_date,
    rank() OVER (ORDER BY tt1.trade_date DESC) AS rank_trade_day
   FROM ( SELECT DISTINCT nse_future_market_tab.trade_date
           FROM public.nse_future_market_tab
          WHERE ((nse_future_market_tab.instrument)::text = 'FUTSTK'::text)) tt1;

ALTER TABLE public.trade_date_order_view1 OWNER TO postgres;


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


CREATE SEQUENCE public.nse_option_market_seq_id
    START WITH 1089705
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.nse_option_market_seq_id OWNER TO postgres;


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
