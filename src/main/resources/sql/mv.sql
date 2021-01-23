create materialized view cfd_data_cd_left_join_f_mv AS
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
         FROM nse_cash_market_tab) cmt
    JOIN ( SELECT nse_delivery_market_tab.symbol,
                  nse_delivery_market_tab.trade_date,
                  nse_delivery_market_tab.traded_qty,
                  nse_delivery_market_tab.deliverable_qty AS delivered_qty,
                  nse_delivery_market_tab.delivery_to_trade_ratio AS del_to_trd_ratio
           FROM nse_delivery_market_tab) dmt ON ((((cmt.symbol)::text = (dmt.symbol)::text) AND (cmt.trade_date = dmt.trade_date))))
     LEFT JOIN ( SELECT nfmt.symbol,
    nfmt.trade_date,
    nfmt.expiry_date,
    nfmt.open AS fuopen,
    nfmt.high AS fuhigh,
    nfmt.low AS fulow,
    nfmt.close AS fuclose,
    nfmt.settle_price AS fulast,
    nfmt.open_int AS oi
FROM nse_future_market_tab nfmt,
    min_expiry_view1 mev
WHERE (((nfmt.symbol)::text = (mev.symbol)::text) AND (nfmt.trade_date = mev.trade_date) AND (nfmt.expiry_date = mev.min_expiry_date))) fmt ON ((((cmt.symbol)::text = (fmt.symbol)::text) AND (cmt.trade_date = fmt.trade_date))));

create view min_expiry_view1 AS
SELECT nse_future_market_tab.symbol,
       nse_future_market_tab.trade_date,
       min(nse_future_market_tab.expiry_date) AS min_expiry_date
FROM nse_future_market_tab
GROUP BY nse_future_market_tab.symbol, nse_future_market_tab.trade_date;

------------------------------------------------------------------------

create view cm_date_linking_view1 AS
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
FROM cm_trade_date_ranking_mv v1,
     cm_trade_date_ranking_mv v2,
     cm_trade_date_ranking_mv v3,
     cm_trade_date_ranking_mv v4,
     cm_trade_date_ranking_mv v5
WHERE (((v1.rank_trade_day + 1) = v2.rank_trade_day) AND ((v2.rank_trade_day + 1) = v3.rank_trade_day) AND ((v3.rank_trade_day + 1) = v4.rank_trade_day) AND ((v4.rank_trade_day + 1) = v5.rank_trade_day));

create materialized view cm_trade_date_ranking_mv AS
SELECT tt1.trade_date,
       rank() OVER (ORDER BY tt1.trade_date DESC) AS rank_trade_day
FROM ( SELECT DISTINCT nse_cash_market_tab.trade_date
       FROM nse_cash_market_tab) tt1;
